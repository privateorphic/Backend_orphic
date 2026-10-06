package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.report.AdminDashboardResponse;
import com.company.employeemanagement.dto.report.EmployeeDashboardResponse;
import com.company.employeemanagement.dto.report.HRDashboardResponse;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.*;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final LoginActivityRepository loginActivityRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final DailyWorkRepository dailyWorkRepository;
    private final SecurityUtils securityUtils;
    private final UserServiceImpl userService;
    private final TaskServiceImpl taskService;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        LocalDate today = LocalDate.now();

        // Counts from real DB
        long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);
        long activeEmployees = userRepository.countByRoleAndStatus(Role.EMPLOYEE, UserStatus.ACTIVE);
        long loggedInToday = loginActivityRepository.countByLoginDateAndStatus(today, LoginStatus.ACTIVE);
        long activeSessions = loginActivityRepository.countByStatus(LoginStatus.ACTIVE);

        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long pendingTasks = taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long cancelledTasks = taskRepository.countByStatus(TaskStatus.CANCELLED);
        long overdueTasks = taskRepository.findOverdueTasks(today).size();

        LocalDate startOfMonth = today.withDayOfMonth(1);
        long newThisMonth = userRepository.countByJoiningDateBetween(startOfMonth, today);

        // Task status distribution
        Map<String, Long> taskStatusDist = new LinkedHashMap<>();
        taskRepository.countByStatusGrouped().forEach(row ->
                taskStatusDist.put(row[0].toString(), (Long) row[1]));

        // Department distribution
        Map<String, Long> deptDist = new LinkedHashMap<>();
        userRepository.countByDepartment().forEach(row ->
                deptDist.put(row[0] != null ? row[0].toString() : "Unassigned", (Long) row[1]));

        // Task priority distribution
        Map<String, Long> priorityDist = new LinkedHashMap<>();
        taskRepository.countByPriorityGrouped().forEach(row ->
                priorityDist.put(row[0].toString(), (Long) row[1]));

        return AdminDashboardResponse.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .inactiveEmployees(totalEmployees - activeEmployees)
                .newEmployeesThisMonth(newThisMonth)
                .loggedInToday(loggedInToday)
                .activeSessions(activeSessions)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .cancelledTasks(cancelledTasks)
                .taskStatusDistribution(taskStatusDist)
                .departmentEmployeeDistribution(deptDist)
                .taskPriorityDistribution(priorityDist)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HRDashboardResponse getHRDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);
        long activeEmployees = userRepository.countByRoleAndStatus(Role.EMPLOYEE, UserStatus.ACTIVE);
        long newThisMonth = userRepository.countByJoiningDateBetween(startOfMonth, today);
        long onLeave = leaveRequestRepository.countEmployeesOnLeaveOnDate(today);
        long loggedInToday = loginActivityRepository.countByLoginDateAndStatus(today, LoginStatus.ACTIVE);
        long pendingLeave = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        long approvedLeave = leaveRequestRepository.countByStatus(LeaveStatus.APPROVED);
        long rejectedLeave = leaveRequestRepository.countByStatus(LeaveStatus.REJECTED);

        // Department distribution
        Map<String, Long> deptDist = new LinkedHashMap<>();
        userRepository.countByDepartment().forEach(row ->
                deptDist.put(row[0] != null ? row[0].toString() : "Unassigned", (Long) row[1]));

        // Leave type distribution
        Map<String, Long> leaveTypeDist = new LinkedHashMap<>();
        leaveRequestRepository.countByLeaveType().forEach(row ->
                leaveTypeDist.put(row[0].toString(), (Long) row[1]));

        return HRDashboardResponse.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .inactiveEmployees(totalEmployees - activeEmployees)
                .newEmployeesThisMonth(newThisMonth)
                .employeesOnLeaveToday(onLeave)
                .loggedInToday(loggedInToday)
                .pendingLeaveRequests(pendingLeave)
                .approvedLeaveRequests(approvedLeave)
                .rejectedLeaveRequests(rejectedLeave)
                .departmentDistribution(deptDist)
                .leaveTypeDistribution(leaveTypeDist)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDashboardResponse getEmployeeDashboard() {
        User user = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();

        // My task counts
        long totalTasks = taskRepository.countByAssignedTo(user);
        long completedTasks = taskRepository.countByAssignedToAndStatus(user, TaskStatus.COMPLETED);
        long pendingTasks = taskRepository.countByAssignedToAndStatus(user, TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByAssignedToAndStatus(user, TaskStatus.IN_PROGRESS);
        long overdueTasks = taskRepository.findOverdueTasks(today).stream()
                .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(user.getId())).count();

        // Today's tasks
        List<TaskResponse> todaysTasks = taskRepository.findByAssignedToAndStatus(user, TaskStatus.TODO).stream()
                .limit(5).map(taskService::mapToResponse).toList();

        // Login info
        var todaySession = loginActivityRepository.findTopByUserAndStatusOrderByCreatedAtDesc(user, LoginStatus.ACTIVE);
        LocalTime loginTime = todaySession.map(la -> la.getLoginDate().equals(today) ? la.getLoginTime() : null).orElse(null);
        boolean loggedIn = todaySession.isPresent();

        // Hours today
        BigDecimal hoursToday = dailyWorkRepository.sumHoursByUserAndDate(user.getId(), today);
        if (hoursToday == null) hoursToday = BigDecimal.ZERO;

        // Hours this week
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        BigDecimal hoursWeek = dailyWorkRepository.sumHoursByUserAndDateRange(user.getId(), weekStart, today);
        if (hoursWeek == null) hoursWeek = BigDecimal.ZERO;

        // Leave
        long pendingLeave = leaveRequestRepository.countByUserAndStatus(user, LeaveStatus.PENDING);
        long approvedLeave = leaveRequestRepository.countByUserAndStatus(user, LeaveStatus.APPROVED);

        return EmployeeDashboardResponse.builder()
                .profile(userService.mapToResponse(user))
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .todaysTasks(todaysTasks)
                .loginTimeToday(loginTime)
                .currentlyLoggedIn(loggedIn)
                .hoursWorkedToday(hoursToday)
                .hoursWorkedThisWeek(hoursWeek)
                .pendingLeaveRequests(pendingLeave)
                .approvedLeaveThisYear(approvedLeave)
                .build();
    }
}
