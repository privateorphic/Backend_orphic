package com.company.employeemanagement.dto.report;

import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class EmployeeDashboardResponse {

    @JsonProperty("profile")
    private EmployeeResponse profile;

    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long overdueTasks;
    private List<TaskResponse> todaysTasks;
    private LocalTime loginTimeToday;
    private boolean currentlyLoggedIn;
    private BigDecimal hoursWorkedToday;
    private BigDecimal hoursWorkedThisWeek;
    private long pendingLeaveRequests;
    private long approvedLeaveThisYear;

    public EmployeeDashboardResponse() {}

    // Aliases for Frontend Compatibility
    @JsonProperty("employee")
    public EmployeeResponse getEmployee() { return profile; }

    @JsonProperty("todayLoginTime")
    public String getTodayLoginTime() { return loginTimeToday != null ? loginTimeToday.toString() : null; }

    @JsonProperty("loggedInToday")
    public boolean isLoggedInToday() { return currentlyLoggedIn; }

    @JsonProperty("todayHoursWorked")
    public BigDecimal getTodayHoursWorked() { return hoursWorkedToday; }

    @JsonProperty("weekHoursWorked")
    public BigDecimal getWeekHoursWorked() { return hoursWorkedThisWeek; }

    public static EmployeeDashboardResponseBuilder builder() { return new EmployeeDashboardResponseBuilder(); }

    public static class EmployeeDashboardResponseBuilder {
        private final EmployeeDashboardResponse r = new EmployeeDashboardResponse();

        public EmployeeDashboardResponseBuilder profile(EmployeeResponse v) { r.profile = v; return this; }
        public EmployeeDashboardResponseBuilder totalTasks(long v) { r.totalTasks = v; return this; }
        public EmployeeDashboardResponseBuilder completedTasks(long v) { r.completedTasks = v; return this; }
        public EmployeeDashboardResponseBuilder pendingTasks(long v) { r.pendingTasks = v; return this; }
        public EmployeeDashboardResponseBuilder inProgressTasks(long v) { r.inProgressTasks = v; return this; }
        public EmployeeDashboardResponseBuilder overdueTasks(long v) { r.overdueTasks = v; return this; }
        public EmployeeDashboardResponseBuilder todaysTasks(List<TaskResponse> v) { r.todaysTasks = v; return this; }
        public EmployeeDashboardResponseBuilder loginTimeToday(LocalTime v) { r.loginTimeToday = v; return this; }
        public EmployeeDashboardResponseBuilder currentlyLoggedIn(boolean v) { r.currentlyLoggedIn = v; return this; }
        public EmployeeDashboardResponseBuilder hoursWorkedToday(BigDecimal v) { r.hoursWorkedToday = v; return this; }
        public EmployeeDashboardResponseBuilder hoursWorkedThisWeek(BigDecimal v) { r.hoursWorkedThisWeek = v; return this; }
        public EmployeeDashboardResponseBuilder pendingLeaveRequests(long v) { r.pendingLeaveRequests = v; return this; }
        public EmployeeDashboardResponseBuilder approvedLeaveThisYear(long v) { r.approvedLeaveThisYear = v; return this; }

        public EmployeeDashboardResponse build() { return r; }
    }

    public EmployeeResponse getProfile() { return profile; }
    public long getTotalTasks() { return totalTasks; }
    public long getCompletedTasks() { return completedTasks; }
    public long getPendingTasks() { return pendingTasks; }
    public long getInProgressTasks() { return inProgressTasks; }
    public long getOverdueTasks() { return overdueTasks; }
    public List<TaskResponse> getTodaysTasks() { return todaysTasks; }
    public LocalTime getLoginTimeToday() { return loginTimeToday; }
    public boolean isCurrentlyLoggedIn() { return currentlyLoggedIn; }
    public BigDecimal getHoursWorkedToday() { return hoursWorkedToday; }
    public BigDecimal getHoursWorkedThisWeek() { return hoursWorkedThisWeek; }
    public long getPendingLeaveRequests() { return pendingLeaveRequests; }
    public long getApprovedLeaveThisYear() { return approvedLeaveThisYear; }
}
