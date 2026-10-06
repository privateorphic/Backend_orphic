package com.company.employeemanagement.dto.report;

import java.util.Map;

public class HRDashboardResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long newEmployeesThisMonth;
    private long employeesOnLeaveToday;
    private long loggedInToday;
    private long pendingLeaveRequests;
    private long approvedLeaveRequests;
    private long rejectedLeaveRequests;
    private Map<String, Long> departmentDistribution;
    private Map<String, Long> leaveTypeDistribution;

    public HRDashboardResponse() {}

    public static HRDashboardResponseBuilder builder() { return new HRDashboardResponseBuilder(); }

    public static class HRDashboardResponseBuilder {
        private final HRDashboardResponse r = new HRDashboardResponse();

        public HRDashboardResponseBuilder totalEmployees(long v) { r.totalEmployees = v; return this; }
        public HRDashboardResponseBuilder activeEmployees(long v) { r.activeEmployees = v; return this; }
        public HRDashboardResponseBuilder inactiveEmployees(long v) { r.inactiveEmployees = v; return this; }
        public HRDashboardResponseBuilder newEmployeesThisMonth(long v) { r.newEmployeesThisMonth = v; return this; }
        public HRDashboardResponseBuilder employeesOnLeaveToday(long v) { r.employeesOnLeaveToday = v; return this; }
        public HRDashboardResponseBuilder loggedInToday(long v) { r.loggedInToday = v; return this; }
        public HRDashboardResponseBuilder pendingLeaveRequests(long v) { r.pendingLeaveRequests = v; return this; }
        public HRDashboardResponseBuilder approvedLeaveRequests(long v) { r.approvedLeaveRequests = v; return this; }
        public HRDashboardResponseBuilder rejectedLeaveRequests(long v) { r.rejectedLeaveRequests = v; return this; }
        public HRDashboardResponseBuilder departmentDistribution(Map<String, Long> v) { r.departmentDistribution = v; return this; }
        public HRDashboardResponseBuilder leaveTypeDistribution(Map<String, Long> v) { r.leaveTypeDistribution = v; return this; }

        public HRDashboardResponse build() { return r; }
    }

    public long getTotalEmployees() { return totalEmployees; }
    public long getActiveEmployees() { return activeEmployees; }
    public long getInactiveEmployees() { return inactiveEmployees; }
    public long getNewEmployeesThisMonth() { return newEmployeesThisMonth; }
    public long getEmployeesOnLeaveToday() { return employeesOnLeaveToday; }
    public long getLoggedInToday() { return loggedInToday; }
    public long getPendingLeaveRequests() { return pendingLeaveRequests; }
    public long getApprovedLeaveRequests() { return approvedLeaveRequests; }
    public long getRejectedLeaveRequests() { return rejectedLeaveRequests; }
    public Map<String, Long> getDepartmentDistribution() { return departmentDistribution; }
    public Map<String, Long> getLeaveTypeDistribution() { return leaveTypeDistribution; }
}
