package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_work", indexes = {
        @Index(name = "idx_daily_work_user", columnList = "user_id"),
        @Index(name = "idx_daily_work_date", columnList = "work_date"),
        @Index(name = "idx_daily_work_task", columnList = "task_id")
})
public class DailyWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "hours_worked", precision = 4, scale = 2)
    private BigDecimal hoursWorked;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TaskStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "report_file_name")
    private String reportFileName;

    @Column(name = "drive_link", length = 500)
    private String driveLink;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DailyWork() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static DailyWorkBuilder builder() { return new DailyWorkBuilder(); }

    public static class DailyWorkBuilder {
        private final DailyWork dw = new DailyWork();

        public DailyWorkBuilder id(Long id) { dw.id = id; return this; }
        public DailyWorkBuilder user(User user) { dw.user = user; return this; }
        public DailyWorkBuilder task(Task task) { dw.task = task; return this; }
        public DailyWorkBuilder workDate(LocalDate workDate) { dw.workDate = workDate; return this; }
        public DailyWorkBuilder description(String description) { dw.description = description; return this; }
        public DailyWorkBuilder hoursWorked(BigDecimal hoursWorked) { dw.hoursWorked = hoursWorked; return this; }
        public DailyWorkBuilder progressPercentage(Integer progressPercentage) { dw.progressPercentage = progressPercentage; return this; }
        public DailyWorkBuilder status(TaskStatus status) { dw.status = status; return this; }
        public DailyWorkBuilder notes(String notes) { dw.notes = notes; return this; }
        public DailyWorkBuilder reportFileName(String reportFileName) { dw.reportFileName = reportFileName; return this; }
        public DailyWorkBuilder driveLink(String driveLink) { dw.driveLink = driveLink; return this; }
        public DailyWorkBuilder createdAt(LocalDateTime createdAt) { dw.createdAt = createdAt; return this; }
        public DailyWorkBuilder updatedAt(LocalDateTime updatedAt) { dw.updatedAt = updatedAt; return this; }

        public DailyWork build() { return dw; }
    }
}
