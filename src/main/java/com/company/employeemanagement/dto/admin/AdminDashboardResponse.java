package com.company.employeemanagement.dto.admin;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    // Employee stats
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;

    // Attendance
    private long loggedInToday;
    private long activeSessions;

    // Task stats
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long overdueTasks;
    private long cancelledTasks;

    // Leave
    private long pendingLeaveRequests;
    private long employeesOnLeave;

    // Charts
    private Map<String, Long> taskStatusDistribution;
    private Map<String, Long> departmentDistribution;
    private List<Map<String, Object>> weeklyTaskCompletion;
    private List<Map<String, Object>> dailyWorkHours;
    private List<Map<String, Object>> recentActivity;
}
