package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_activity_history", indexes = {
        @Index(name = "idx_activity_task", columnList = "task_id"),
        @Index(name = "idx_activity_emp", columnList = "employee_id"),
        @Index(name = "idx_activity_attendance", columnList = "attendance_id")
})
public class TaskActivityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private DailyAttendance attendance;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private ActivityActionType actionType;

    @Column(name = "old_status", length = 30)
    private String oldStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    @Column(name = "old_progress")
    private Integer oldProgress;

    @Column(name = "new_progress")
    private Integer newProgress;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TaskActivityHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public DailyAttendance getAttendance() { return attendance; }
    public void setAttendance(DailyAttendance attendance) { this.attendance = attendance; }

    public ActivityActionType getActionType() { return actionType; }
    public void setActionType(ActivityActionType actionType) { this.actionType = actionType; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public Integer getOldProgress() { return oldProgress; }
    public void setOldProgress(Integer oldProgress) { this.oldProgress = oldProgress; }

    public Integer getNewProgress() { return newProgress; }
    public void setNewProgress(Integer newProgress) { this.newProgress = newProgress; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
