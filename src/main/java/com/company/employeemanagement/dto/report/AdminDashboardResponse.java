package com.company.employeemanagement.dto.report;

import java.util.Map;

public class AdminDashboardResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long newEmployeesThisMonth;
    private long loggedInToday;
    private long activeSessions;
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long overdueTasks;
    private long cancelledTasks;
    private Map<String, Long> taskStatusDistribution;
    private Map<String, Long> departmentEmployeeDistribution;
    private Map<String, Long> taskPriorityDistribution;

    public AdminDashboardResponse() {}

    public static AdminDashboardResponseBuilder builder() { return new AdminDashboardResponseBuilder(); }

    public static class AdminDashboardResponseBuilder {
        private final AdminDashboardResponse r = new AdminDashboardResponse();

        public AdminDashboardResponseBuilder totalEmployees(long v) { r.totalEmployees = v; return this; }
        public AdminDashboardResponseBuilder activeEmployees(long v) { r.activeEmployees = v; return this; }
        public AdminDashboardResponseBuilder inactiveEmployees(long v) { r.inactiveEmployees = v; return this; }
        public AdminDashboardResponseBuilder newEmployeesThisMonth(long v) { r.newEmployeesThisMonth = v; return this; }
        public AdminDashboardResponseBuilder loggedInToday(long v) { r.loggedInToday = v; return this; }
        public AdminDashboardResponseBuilder activeSessions(long v) { r.activeSessions = v; return this; }
        public AdminDashboardResponseBuilder totalTasks(long v) { r.totalTasks = v; return this; }
        public AdminDashboardResponseBuilder completedTasks(long v) { r.completedTasks = v; return this; }
        public AdminDashboardResponseBuilder pendingTasks(long v) { r.pendingTasks = v; return this; }
        public AdminDashboardResponseBuilder inProgressTasks(long v) { r.inProgressTasks = v; return this; }
        public AdminDashboardResponseBuilder overdueTasks(long v) { r.overdueTasks = v; return this; }
        public AdminDashboardResponseBuilder cancelledTasks(long v) { r.cancelledTasks = v; return this; }
        public AdminDashboardResponseBuilder taskStatusDistribution(Map<String, Long> v) { r.taskStatusDistribution = v; return this; }
        public AdminDashboardResponseBuilder departmentEmployeeDistribution(Map<String, Long> v) { r.departmentEmployeeDistribution = v; return this; }
        public AdminDashboardResponseBuilder taskPriorityDistribution(Map<String, Long> v) { r.taskPriorityDistribution = v; return this; }

        public AdminDashboardResponse build() { return r; }
    }

    public long getTotalEmployees() { return totalEmployees; }
    public long getActiveEmployees() { return activeEmployees; }
    public long getInactiveEmployees() { return inactiveEmployees; }
    public long getNewEmployeesThisMonth() { return newEmployeesThisMonth; }
    public long getLoggedInToday() { return loggedInToday; }
    public long getActiveSessions() { return activeSessions; }
    public long getTotalTasks() { return totalTasks; }
    public long getCompletedTasks() { return completedTasks; }
    public long getPendingTasks() { return pendingTasks; }
    public long getInProgressTasks() { return inProgressTasks; }
    public long getOverdueTasks() { return overdueTasks; }
    public long getCancelledTasks() { return cancelledTasks; }
    public Map<String, Long> getTaskStatusDistribution() { return taskStatusDistribution; }
    public Map<String, Long> getDepartmentEmployeeDistribution() { return departmentEmployeeDistribution; }
    public Map<String, Long> getTaskPriorityDistribution() { return taskPriorityDistribution; }
}
