package com.company.employeemanagement.dto.leave;

import com.company.employeemanagement.entity.LeaveStatus;
import com.company.employeemanagement.entity.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveResponse {
    private Long id;
    private Long userId;
    private String employeeName;
    private String employeeId;
    private String departmentName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private String reason;
    private LeaveStatus status;
    private String reviewerName;
    private String reviewerComments;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public LeaveResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

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

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getReviewerComments() { return reviewerComments; }
    public void setReviewerComments(String reviewerComments) { this.reviewerComments = reviewerComments; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static LeaveResponseBuilder builder() { return new LeaveResponseBuilder(); }

    public static class LeaveResponseBuilder {
        private final LeaveResponse r = new LeaveResponse();

        public LeaveResponseBuilder id(Long v) { r.id = v; return this; }
        public LeaveResponseBuilder userId(Long v) { r.userId = v; return this; }
        public LeaveResponseBuilder employeeName(String v) { r.employeeName = v; return this; }
        public LeaveResponseBuilder employeeId(String v) { r.employeeId = v; return this; }
        public LeaveResponseBuilder departmentName(String v) { r.departmentName = v; return this; }
        public LeaveResponseBuilder leaveType(LeaveType v) { r.leaveType = v; return this; }
        public LeaveResponseBuilder startDate(LocalDate v) { r.startDate = v; return this; }
        public LeaveResponseBuilder endDate(LocalDate v) { r.endDate = v; return this; }
        public LeaveResponseBuilder totalDays(Integer v) { r.totalDays = v; return this; }
        public LeaveResponseBuilder reason(String v) { r.reason = v; return this; }
        public LeaveResponseBuilder status(LeaveStatus v) { r.status = v; return this; }
        public LeaveResponseBuilder reviewerName(String v) { r.reviewerName = v; return this; }
        public LeaveResponseBuilder reviewerComments(String v) { r.reviewerComments = v; return this; }
        public LeaveResponseBuilder reviewedAt(LocalDateTime v) { r.reviewedAt = v; return this; }
        public LeaveResponseBuilder createdAt(LocalDateTime v) { r.createdAt = v; return this; }

        public LeaveResponse build() { return r; }
    }
}
