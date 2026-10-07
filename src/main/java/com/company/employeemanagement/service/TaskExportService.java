package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.report.TaskExportFilterRequest;
import com.company.employeemanagement.entity.User;

import java.io.ByteArrayInputStream;

public interface TaskExportService {
    ByteArrayInputStream exportTasksToExcel(TaskExportFilterRequest filter, User currentUser);
}
