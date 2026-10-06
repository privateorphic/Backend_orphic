package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.dailywork.DailyWorkRequest;
import com.company.employeemanagement.dto.dailywork.DailyWorkResponse;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.DailyWorkRepository;
import com.company.employeemanagement.repository.TaskRepository;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.AuditLogService;
import com.company.employeemanagement.service.DailyWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyWorkServiceImpl implements DailyWorkService {

    private final DailyWorkRepository dailyWorkRepository;
    private final TaskRepository taskRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public DailyWorkResponse submitDailyWork(DailyWorkRequest request) {
        User user = securityUtils.getCurrentUser(); // Always use authenticated user

        Task task = null;
        if (request.getTaskId() != null) {
            task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", request.getTaskId()));
        }

        DailyWork dailyWork = DailyWork.builder()
                .user(user)
                .task(task)
                .workDate(request.getWorkDate())
                .description(request.getDescription())
                .hoursWorked(request.getHoursWorked())
                .progressPercentage(request.getProgressPercentage())
                .status(request.getStatus())
                .notes(request.getNotes())
                .reportFileName(request.getReportFileName())
                .driveLink(request.getDriveLink())
                .build();

        DailyWork saved = dailyWorkRepository.save(dailyWork);

        // Update task progress if associated
        if (task != null && request.getProgressPercentage() != null) {
            task.setProgressPercentage(request.getProgressPercentage());
            if (request.getStatus() != null) task.setStatus(request.getStatus());
            taskRepository.save(task);
        }

        auditLogService.log(user, AuditAction.DAILY_WORK_SUBMITTED, "DailyWork", saved.getId(),
                "Daily work submitted for " + request.getWorkDate());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyWorkResponse> getMyDailyWork() {
        User user = securityUtils.getCurrentUser();
        return dailyWorkRepository.findByUserOrderByWorkDateDesc(user).stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DailyWorkResponse> getMyWorkHistory(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        return dailyWorkRepository.findByUserOrderByWorkDateDesc(user, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DailyWorkResponse> getAllDailyWork(Pageable pageable) {
        return dailyWorkRepository.findAll(pageable).map(this::mapToResponse);
    }

    private DailyWorkResponse mapToResponse(DailyWork dw) {
        return DailyWorkResponse.builder()
                .id(dw.getId())
                .userId(dw.getUser().getId())
                .employeeName(dw.getUser().getName())
                .employeeId(dw.getUser().getEmployeeId())
                .taskId(dw.getTask() != null ? dw.getTask().getId() : null)
                .taskTitle(dw.getTask() != null ? dw.getTask().getTitle() : null)
                .workDate(dw.getWorkDate())
                .description(dw.getDescription())
                .hoursWorked(dw.getHoursWorked())
                .progressPercentage(dw.getProgressPercentage())
                .status(dw.getStatus())
                .notes(dw.getNotes())
                .reportFileName(dw.getReportFileName())
                .driveLink(dw.getDriveLink())
                .createdAt(dw.getCreatedAt())
                .build();
    }
}
