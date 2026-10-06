package com.company.employeemanagement.dto.task;

import com.company.employeemanagement.entity.TaskPriority;
import com.company.employeemanagement.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Long assignedToId;
    private String assignedToName;
    private String assignedToEmployeeId;
    private Long createdById;
    private String createdByName;
    private Long departmentId;
    private String departmentName;
    private TaskPriority priority;
    private TaskStatus status;
    private Integer progressPercentage;
    private LocalDate startDate;
    private LocalDate deadline;
    private LocalDateTime completedAt;
    private String workUpdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public String getAssignedToEmployeeId() { return assignedToEmployeeId; }
    public void setAssignedToEmployeeId(String assignedToEmployeeId) { this.assignedToEmployeeId = assignedToEmployeeId; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getWorkUpdate() { return workUpdate; }
    public void setWorkUpdate(String workUpdate) { this.workUpdate = workUpdate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static TaskResponseBuilder builder() { return new TaskResponseBuilder(); }

    public static class TaskResponseBuilder {
        private final TaskResponse r = new TaskResponse();

        public TaskResponseBuilder id(Long v) { r.id = v; return this; }
        public TaskResponseBuilder title(String v) { r.title = v; return this; }
        public TaskResponseBuilder description(String v) { r.description = v; return this; }
        public TaskResponseBuilder assignedToId(Long v) { r.assignedToId = v; return this; }
        public TaskResponseBuilder assignedToName(String v) { r.assignedToName = v; return this; }
        public TaskResponseBuilder assignedToEmployeeId(String v) { r.assignedToEmployeeId = v; return this; }
        public TaskResponseBuilder createdById(Long v) { r.createdById = v; return this; }
        public TaskResponseBuilder createdByName(String v) { r.createdByName = v; return this; }
        public TaskResponseBuilder departmentId(Long v) { r.departmentId = v; return this; }
        public TaskResponseBuilder departmentName(String v) { r.departmentName = v; return this; }
        public TaskResponseBuilder priority(TaskPriority v) { r.priority = v; return this; }
        public TaskResponseBuilder status(TaskStatus v) { r.status = v; return this; }
        public TaskResponseBuilder progressPercentage(Integer v) { r.progressPercentage = v; return this; }
        public TaskResponseBuilder startDate(LocalDate v) { r.startDate = v; return this; }
        public TaskResponseBuilder deadline(LocalDate v) { r.deadline = v; return this; }
        public TaskResponseBuilder completedAt(LocalDateTime v) { r.completedAt = v; return this; }
        public TaskResponseBuilder workUpdate(String v) { r.workUpdate = v; return this; }
        public TaskResponseBuilder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public TaskResponseBuilder updatedAt(LocalDateTime v) { r.updatedAt = v; return this; }

        public TaskResponse build() { return r; }
    }
}
