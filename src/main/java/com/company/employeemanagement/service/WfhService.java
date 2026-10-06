package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.wfh.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface WfhService {
    WfhResponse createWfhRequest(WfhRequestDto dto);
    List<WfhResponse> getMyWfhRequests();
    Page<WfhResponse> getAllWfhRequests(LocalDate date, String status, Pageable pageable);
    WfhResponse approveWfhRequest(Long id, WfhApprovalRequest approvalRequest);
    WfhResponse rejectWfhRequest(Long id, WfhApprovalRequest approvalRequest);
    WfhResponse cancelWfhRequest(Long id);

    void recordLocation(WfhLocationRequest locationRequest);
    WfhActiveEmployeeDto getLatestLocation(Long employeeId);
    List<WfhActiveEmployeeDto> getLocationHistory(Long employeeId);

    List<WfhActiveEmployeeDto> getActiveWfhEmployees();
}
