package com.company.employeemanagement.dto.wfh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WfhApprovalRequest {
    private String comments;
    private String rejectionReason;

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
