package com.company.employeemanagement.mapper;

import com.company.employeemanagement.dto.leave.LeaveResponse;
import com.company.employeemanagement.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    public LeaveResponse toLeaveResponse(LeaveRequest leave) {
        if (leave == null) return null;
        return LeaveResponse.builder()
                .id(leave.getId())
                .userId(leave.getUser() != null ? leave.getUser().getId() : null)
                .employeeName(leave.getUser() != null ? leave.getUser().getName() : null)
                .employeeId(leave.getUser() != null ? leave.getUser().getEmployeeId() : null)
                .departmentName(leave.getUser() != null && leave.getUser().getDepartment() != null
                        ? leave.getUser().getDepartment().getName() : null)
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .reviewerName(leave.getReviewedBy() != null ? leave.getReviewedBy().getName() : null)
                .reviewerComments(leave.getReviewerComments())
                .reviewedAt(leave.getReviewedAt())
                .createdAt(leave.getCreatedAt())
                .build();
    }
}
