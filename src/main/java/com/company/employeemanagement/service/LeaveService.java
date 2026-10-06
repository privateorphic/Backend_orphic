package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.leave.LeaveApplicationRequest;
import com.company.employeemanagement.dto.leave.LeaveApprovalRequest;
import com.company.employeemanagement.dto.leave.LeaveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveService {
    LeaveResponse applyLeave(LeaveApplicationRequest request);
    Page<LeaveResponse> getMyLeaves(Pageable pageable);
    LeaveResponse cancelLeave(Long leaveId);

    // HR endpoints
    Page<LeaveResponse> getAllLeaves(Pageable pageable);
    LeaveResponse approveLeave(Long leaveId, LeaveApprovalRequest request);
    LeaveResponse rejectLeave(Long leaveId, LeaveApprovalRequest request);
}
