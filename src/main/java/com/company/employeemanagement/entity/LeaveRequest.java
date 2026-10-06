package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests", indexes = {
        @Index(name = "idx_leave_user", columnList = "user_id"),
        @Index(name = "idx_leave_status", columnList = "status"),
        @Index(name = "idx_leave_type", columnList = "leave_type")
})
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 30)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewer_comments", columnDefinition = "TEXT")
    private String reviewerComments;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LeaveRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewerComments() { return reviewerComments; }
    public void setReviewerComments(String reviewerComments) { this.reviewerComments = reviewerComments; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static LeaveRequestBuilder builder() { return new LeaveRequestBuilder(); }

    public static class LeaveRequestBuilder {
        private final LeaveRequest lr = new LeaveRequest();

        public LeaveRequestBuilder id(Long id) { lr.id = id; return this; }
        public LeaveRequestBuilder user(User user) { lr.user = user; return this; }
        public LeaveRequestBuilder leaveType(LeaveType leaveType) { lr.leaveType = leaveType; return this; }
        public LeaveRequestBuilder startDate(LocalDate startDate) { lr.startDate = startDate; return this; }
        public LeaveRequestBuilder endDate(LocalDate endDate) { lr.endDate = endDate; return this; }
        public LeaveRequestBuilder totalDays(Integer totalDays) { lr.totalDays = totalDays; return this; }
        public LeaveRequestBuilder reason(String reason) { lr.reason = reason; return this; }
        public LeaveRequestBuilder status(LeaveStatus status) { lr.status = status; return this; }
        public LeaveRequestBuilder reviewedBy(User reviewedBy) { lr.reviewedBy = reviewedBy; return this; }
        public LeaveRequestBuilder reviewerComments(String reviewerComments) { lr.reviewerComments = reviewerComments; return this; }
        public LeaveRequestBuilder reviewedAt(LocalDateTime reviewedAt) { lr.reviewedAt = reviewedAt; return this; }
        public LeaveRequestBuilder createdAt(LocalDateTime createdAt) { lr.createdAt = createdAt; return this; }
        public LeaveRequestBuilder updatedAt(LocalDateTime updatedAt) { lr.updatedAt = updatedAt; return this; }

        public LeaveRequest build() { return lr; }
    }
}
