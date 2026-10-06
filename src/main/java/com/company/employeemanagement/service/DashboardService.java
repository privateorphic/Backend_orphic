package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.report.AdminDashboardResponse;
import com.company.employeemanagement.dto.report.EmployeeDashboardResponse;
import com.company.employeemanagement.dto.report.HRDashboardResponse;

public interface DashboardService {
    AdminDashboardResponse getAdminDashboard();
    HRDashboardResponse getHRDashboard();
    EmployeeDashboardResponse getEmployeeDashboard();
}
