package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.task.CreateTaskRequest;
import com.company.employeemanagement.dto.task.EmployeeTaskUpdateRequest;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.company.employeemanagement.dto.task.UpdateTaskRequest;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.exception.UnauthorizedException;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.TaskRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.AuditLogService;
import com.company.employeemanagement.service.NotificationService;
import com.company.employeemanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import com.company.employeemanagement.repository.DailyAttendanceRepository;
import com.company.employeemanagement.service.TaskActivityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final TaskActivityService taskActivityService;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(assignedTo)
                .createdBy(currentUser)
                .department(department)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .status(TaskStatus.TODO)
                .progressPercentage(0)
                .startDate(request.getStartDate())
                .deadline(request.getDeadline())
                .build();

        Task saved = taskRepository.save(task);

        auditLogService.log(currentUser, AuditAction.TASK_CREATED, "Task", saved.getId(), "Task created: " + saved.getTitle());

        if (assignedTo != null) {
            notificationService.createNotification(assignedTo, NotificationType.TASK_ASSIGNED,
                    "New Task Assigned",
                    "You have been assigned a new task: " + saved.getTitle(),
                    saved.getId(), "Task");
            auditLogService.log(currentUser, AuditAction.TASK_ASSIGNED, "Task", saved.getId(),
                    "Task assigned to " + assignedTo.getEmployeeId());
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse createEmployeeTask(CreateTaskRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        User assignedTo = currentUser;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElse(currentUser);
        }

        Department department = currentUser.getDepartment();
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElse(department);
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(assignedTo)
                .createdBy(currentUser)
                .department(department)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .status(TaskStatus.TODO)
                .progressPercentage(0)
                .startDate(request.getStartDate())
                .deadline(request.getDeadline())
                .build();

        Task saved = taskRepository.save(task);
        auditLogService.log(currentUser, AuditAction.TASK_CREATED, "Task", saved.getId(), "Employee created task: " + saved.getTitle());

        DailyAttendance att = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, LocalDate.now()).orElse(null);
        taskActivityService.logActivity(saved, currentUser, att, ActivityActionType.TASK_CREATED, null, saved.getStatus().name(), 0, saved.getProgressPercentage(), "Task created: " + saved.getTitle());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = findById(id);
        User currentUser = securityUtils.getCurrentUser();

        String oldStatusStr = task.getStatus() != null ? task.getStatus().name() : null;
        Integer oldProgressVal = task.getProgressPercentage();

        if (StringUtils.hasText(request.getTitle())) task.setTitle(request.getTitle());
        if (StringUtils.hasText(request.getDescription())) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getStartDate() != null) task.setStartDate(request.getStartDate());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (StringUtils.hasText(request.getWorkUpdate())) task.setWorkUpdate(request.getWorkUpdate());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
                task.setProgressPercentage(100);
            }
        }
        if (request.getProgressPercentage() != null) task.setProgressPercentage(request.getProgressPercentage());

        if (request.getAssignedToId() != null) {
            User newAssignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            task.setAssignedTo(newAssignee);
            notificationService.createNotification(newAssignee, NotificationType.TASK_ASSIGNED,
                    "Task Reassigned", "Task '" + task.getTitle() + "' has been assigned to you.", task.getId(), "Task");
        }

        if (request.getDepartmentId() != null) {
            task.setDepartment(departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId())));
        }

        Task saved = taskRepository.save(task);
        auditLogService.log(currentUser, AuditAction.TASK_UPDATED, "Task", saved.getId(), "Task updated: " + saved.getTitle());

        User assignee = saved.getAssignedTo() != null ? saved.getAssignedTo() : currentUser;
        DailyAttendance att = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(assignee, LocalDate.now()).orElse(null);
        ActivityActionType actionType = (saved.getStatus() == TaskStatus.COMPLETED) ? ActivityActionType.TASK_COMPLETED : ActivityActionType.TASK_UPDATED;
        taskActivityService.logActivity(saved, assignee, att, actionType, oldStatusStr, saved.getStatus().name(), oldProgressVal, saved.getProgressPercentage(), "Task updated: " + saved.getTitle());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = findById(id);
        User currentUser = securityUtils.getCurrentUser();

        try {
            jdbcTemplate.update("UPDATE daily_work SET task_id = NULL WHERE task_id = ?", id);
            jdbcTemplate.update("UPDATE manager_feedback SET task_id = NULL WHERE task_id = ?", id);
        } catch (Exception e) {
            log.debug("Task reference cleanup error: {}", e.getMessage());
        }

        taskRepository.delete(task);
        auditLogService.log(currentUser, AuditAction.TASK_DELETED, "Task", id, "Task deleted: " + task.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks() {
        User user = securityUtils.getCurrentUser();
        return taskRepository.findByAssignedTo(user).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getMyTask(Long id) {
        User user = securityUtils.getCurrentUser();
        Task task = findById(id);
        if (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have access to this task");
        }
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateMyTaskProgress(Long id, EmployeeTaskUpdateRequest request) {
        User user = securityUtils.getCurrentUser();
        Task task = findById(id);

        if (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only update tasks assigned to you");
        }

        String oldStatusStr = task.getStatus() != null ? task.getStatus().name() : null;
        Integer oldProgressVal = task.getProgressPercentage();

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
                task.setProgressPercentage(100);
            }
        }
        if (request.getProgressPercentage() != null) task.setProgressPercentage(request.getProgressPercentage());
        if (StringUtils.hasText(request.getWorkUpdate())) task.setWorkUpdate(request.getWorkUpdate());

        Task saved = taskRepository.save(task);
        auditLogService.log(user, AuditAction.TASK_UPDATED, "Task", saved.getId(), "Employee updated task progress: " + saved.getTitle());

        DailyAttendance att = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(user, LocalDate.now()).orElse(null);
        ActivityActionType actionType = (saved.getStatus() == TaskStatus.COMPLETED) ? ActivityActionType.TASK_COMPLETED : ActivityActionType.TASK_UPDATED;
        taskActivityService.logActivity(saved, user, att, actionType, oldStatusStr, saved.getStatus().name(), oldProgressVal, saved.getProgressPercentage(), "Task progress updated: " + saved.getTitle());

        return mapToResponse(saved);
    }

    private Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    public TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null)
                .assignedToEmployeeId(task.getAssignedTo() != null ? task.getAssignedTo().getEmployeeId() : null)
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .createdByName(task.getCreatedBy() != null ? task.getCreatedBy().getName() : null)
                .departmentId(task.getDepartment() != null ? task.getDepartment().getId() : null)
                .departmentName(task.getDepartment() != null ? task.getDepartment().getName() : null)
                .priority(task.getPriority())
                .status(task.getStatus())
                .progressPercentage(task.getProgressPercentage())
                .startDate(task.getStartDate())
                .deadline(task.getDeadline())
                .completedAt(task.getCompletedAt())
                .workUpdate(task.getWorkUpdate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
