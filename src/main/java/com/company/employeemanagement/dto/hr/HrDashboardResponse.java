package com.company.employeemanagement.dto.hr;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrDashboardResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long newEmployeesThisMonth;
    private long loggedInToday;
    private long employeesOnLeave;
    private long pendingLeaveRequests;

    private Map<String, Long> departmentDistribution;
    private Map<String, Long> leaveDistribution;
    private List<Map<String, Object>> attendanceTrend;
    private List<Map<String, Object>> recentLeaveRequests;
}
