package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "daily_task_snapshots", indexes = {
        @Index(name = "idx_snapshot_emp", columnList = "employee_id"),
        @Index(name = "idx_snapshot_attendance", columnList = "attendance_id"),
        @Index(name = "idx_snapshot_type", columnList = "snapshot_type")
})
public class DailyTaskSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private DailyAttendance attendance;

    @Enumerated(EnumType.STRING)
    @Column(name = "snapshot_type", nullable = false, length = 20)
    private SnapshotType snapshotType;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "task_title", nullable = false, length = 250)
    private String taskTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false, length = 20)
    private TaskStatus taskStatus;

    @Column(name = "task_progress")
    private Integer taskProgress;

    @Column(name = "snapshot_time", nullable = false)
    private LocalDateTime snapshotTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public DailyTaskSnapshot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public DailyAttendance getAttendance() { return attendance; }
    public void setAttendance(DailyAttendance attendance) { this.attendance = attendance; }

    public SnapshotType getSnapshotType() { return snapshotType; }
    public void setSnapshotType(SnapshotType snapshotType) { this.snapshotType = snapshotType; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public TaskStatus getTaskStatus() { return taskStatus; }
    public void setTaskStatus(TaskStatus taskStatus) { this.taskStatus = taskStatus; }

    public Integer getTaskProgress() { return taskProgress; }
    public void setTaskProgress(Integer taskProgress) { this.taskProgress = taskProgress; }

    public LocalDateTime getSnapshotTime() { return snapshotTime; }
    public void setSnapshotTime(LocalDateTime snapshotTime) { this.snapshotTime = snapshotTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
