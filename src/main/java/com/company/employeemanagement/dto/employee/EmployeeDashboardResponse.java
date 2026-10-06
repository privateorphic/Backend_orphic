package com.company.employeemanagement.dto.employee;

import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import com.company.employeemanagement.dto.task.TaskResponse;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDashboardResponse {

    // Profile
    private EmployeeResponse employee;

    // Today's attendance
    private LocalTime todayLoginTime;
    private String currentSessionDuration;
    private boolean loggedInToday;

    // Task summary
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long overdueTasks;

    // Today's tasks
    private List<TaskResponse> todaysTasks;

    // Work hours
    private Double todayHoursWorked;
    private Double weekHoursWorked;

    // Recent activity
    private List<LoginActivityResponse> recentSessions;
}
