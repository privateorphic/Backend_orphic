package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.leave.LeaveApplicationRequest;
import com.company.employeemanagement.dto.leave.LeaveApprovalRequest;
import com.company.employeemanagement.dto.leave.LeaveResponse;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.exception.UnauthorizedException;
import com.company.employeemanagement.repository.LeaveRequestRepository;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.AuditLogService;
import com.company.employeemanagement.service.LeaveService;
import com.company.employeemanagement.service.NotificationService;
import com.company.employeemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LeaveResponse applyLeave(LeaveApplicationRequest request) {
        User user = securityUtils.getCurrentUser();

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        int totalDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .user(user)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays(totalDays)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        auditLogService.log(user, AuditAction.LEAVE_APPLIED, "LeaveRequest", saved.getId(),
                "Leave applied: " + request.getLeaveType() + " for " + totalDays + " days");

        // Notify all HR & Admin users
        userRepository.findByRole(Role.HR).forEach(hr ->
                notificationService.createNotification(hr, NotificationType.LEAVE_REQUEST,
                        "Leave Request",
                        user.getName() + " applied for " + request.getLeaveType() + " leave for " + totalDays + " days",
                        saved.getId(), "LeaveRequest"));
        userRepository.findByRole(Role.ADMIN).forEach(admin ->
                notificationService.createNotification(admin, NotificationType.LEAVE_REQUEST,
                        "Leave Request",
                        user.getName() + " applied for " + request.getLeaveType() + " leave for " + totalDays + " days",
                        saved.getId(), "LeaveRequest"));

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getMyLeaves(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        return leaveRequestRepository.findByUserOrderByCreatedAtDesc(user, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public LeaveResponse cancelLeave(Long leaveId) {
        User user = securityUtils.getCurrentUser();
        LeaveRequest leave = findById(leaveId);

        if (!leave.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only cancel your own leave requests");
        }
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest saved = leaveRequestRepository.save(leave);
        auditLogService.log(user, AuditAction.LEAVE_CANCELLED, "LeaveRequest", leaveId, "Leave cancelled by employee");
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getAllLeaves(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public LeaveResponse approveLeave(Long leaveId, LeaveApprovalRequest request) {
        User reviewer = securityUtils.getCurrentUser();
        LeaveRequest leave = findById(leaveId);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setReviewedBy(reviewer);
        leave.setReviewerComments(request != null ? request.getReviewerComments() : null);
        leave.setReviewedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(leave);

        auditLogService.log(reviewer, AuditAction.LEAVE_APPROVED, "LeaveRequest", leaveId, "Leave approved by " + reviewer.getEmployeeId());

        notificationService.createNotification(leave.getUser(), NotificationType.LEAVE_APPROVED,
                "Leave Approved", "Your " + leave.getLeaveType() + " leave request has been approved.",
                leaveId, "LeaveRequest");

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId, LeaveApprovalRequest request) {
        User reviewer = securityUtils.getCurrentUser();
        LeaveRequest leave = findById(leaveId);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setReviewedBy(reviewer);
        leave.setReviewerComments(request != null ? request.getReviewerComments() : null);
        leave.setReviewedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(leave);

        auditLogService.log(reviewer, AuditAction.LEAVE_REJECTED, "LeaveRequest", leaveId, "Leave rejected by " + reviewer.getEmployeeId());

        notificationService.createNotification(leave.getUser(), NotificationType.LEAVE_REJECTED,
                "Leave Rejected", "Your " + leave.getLeaveType() + " leave request has been rejected. Reason: " + (request != null ? request.getReviewerComments() : ""),
                leaveId, "LeaveRequest");

        return mapToResponse(saved);
    }

    private LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", id));
    }

    private LeaveResponse mapToResponse(LeaveRequest lr) {
        return LeaveResponse.builder()
                .id(lr.getId())
                .userId(lr.getUser().getId())
                .employeeName(lr.getUser().getName())
                .employeeId(lr.getUser().getEmployeeId())
                .departmentName(lr.getUser().getDepartment() != null ? lr.getUser().getDepartment().getName() : null)
                .leaveType(lr.getLeaveType())
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .totalDays(lr.getTotalDays())
                .reason(lr.getReason())
                .status(lr.getStatus())
                .reviewerName(lr.getReviewedBy() != null ? lr.getReviewedBy().getName() : null)
                .reviewerComments(lr.getReviewerComments())
                .reviewedAt(lr.getReviewedAt())
                .createdAt(lr.getCreatedAt())
                .build();
    }
}
