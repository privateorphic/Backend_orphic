package com.company.employeemanagement.dto.report;

import com.company.employeemanagement.entity.TaskPriority;
import com.company.employeemanagement.entity.TaskStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TaskExportFilterRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    private Long employeeId;
    private Long departmentId;
    private TaskStatus status;
    private TaskPriority priority;

    public TaskExportFilterRequest() {}

    public TaskExportFilterRequest(LocalDate fromDate, LocalDate toDate, Long employeeId, Long departmentId, TaskStatus status, TaskPriority priority) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.status = status;
        this.priority = priority;
    }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
}
