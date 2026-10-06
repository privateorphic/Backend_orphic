package com.company.employeemanagement.dto.dailywork;

import com.company.employeemanagement.entity.TaskStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyWorkResponse {
    private Long id;
    private Long userId;
    private String employeeName;
    private String employeeId;
    private Long taskId;
    private String taskTitle;
    private LocalDate workDate;
    private String description;
    private BigDecimal hoursWorked;
    private Integer progressPercentage;
    private TaskStatus status;
    private String notes;
    private String reportFileName;
    private String driveLink;
    private LocalDateTime createdAt;

    public DailyWorkResponse() {}

    public DailyWorkResponse(Long id, Long userId, String employeeName, String employeeId, Long taskId, String taskTitle,
                             LocalDate workDate, String description, BigDecimal hoursWorked, Integer progressPercentage,
                             TaskStatus status, String notes, String reportFileName, String driveLink, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.workDate = workDate;
        this.description = description;
        this.hoursWorked = hoursWorked;
        this.progressPercentage = progressPercentage;
        this.status = status;
        this.notes = notes;
        this.reportFileName = reportFileName;
        this.driveLink = driveLink;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(BigDecimal hoursWorked) { this.hoursWorked = hoursWorked; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getReportFileName() { return reportFileName; }
    public void setReportFileName(String reportFileName) { this.reportFileName = reportFileName; }

    public String getDriveLink() { return driveLink; }
    public void setDriveLink(String driveLink) { this.driveLink = driveLink; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static DailyWorkResponseBuilder builder() { return new DailyWorkResponseBuilder(); }

    public static class DailyWorkResponseBuilder {
        private Long id;
        private Long userId;
        private String employeeName;
        private String employeeId;
        private Long taskId;
        private String taskTitle;
        private LocalDate workDate;
        private String description;
        private BigDecimal hoursWorked;
        private Integer progressPercentage;
        private TaskStatus status;
        private String notes;
        private String reportFileName;
        private String driveLink;
        private LocalDateTime createdAt;

        public DailyWorkResponseBuilder id(Long id) { this.id = id; return this; }
        public DailyWorkResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public DailyWorkResponseBuilder employeeName(String employeeName) { this.employeeName = employeeName; return this; }
        public DailyWorkResponseBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public DailyWorkResponseBuilder taskId(Long taskId) { this.taskId = taskId; return this; }
        public DailyWorkResponseBuilder taskTitle(String taskTitle) { this.taskTitle = taskTitle; return this; }
        public DailyWorkResponseBuilder workDate(LocalDate workDate) { this.workDate = workDate; return this; }
        public DailyWorkResponseBuilder description(String description) { this.description = description; return this; }
        public DailyWorkResponseBuilder hoursWorked(BigDecimal hoursWorked) { this.hoursWorked = hoursWorked; return this; }
        public DailyWorkResponseBuilder progressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; return this; }
        public DailyWorkResponseBuilder status(TaskStatus status) { this.status = status; return this; }
        public DailyWorkResponseBuilder notes(String notes) { this.notes = notes; return this; }
        public DailyWorkResponseBuilder reportFileName(String reportFileName) { this.reportFileName = reportFileName; return this; }
        public DailyWorkResponseBuilder driveLink(String driveLink) { this.driveLink = driveLink; return this; }
        public DailyWorkResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DailyWorkResponse build() {
            return new DailyWorkResponse(id, userId, employeeName, employeeId, taskId, taskTitle, workDate, description, hoursWorked, progressPercentage, status, notes, reportFileName, driveLink, createdAt);
        }
    }
}
