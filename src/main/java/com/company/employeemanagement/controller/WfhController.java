package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.ApiResponse;
import com.company.employeemanagement.dto.wfh.*;
import com.company.employeemanagement.service.WfhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/wfh", "/api/v1/wfh"})
@RequiredArgsConstructor
public class WfhController {

    private final WfhService wfhService;

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<WfhResponse>> createWfhRequest(@Valid @RequestBody WfhRequestDto dto) {
        WfhResponse response = wfhService.createWfhRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("WFH request submitted successfully", response));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<WfhResponse>>> getMyWfhRequests() {
        List<WfhResponse> response = wfhService.getMyWfhRequests();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<WfhResponse>>> getAllWfhRequests(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<WfhResponse> response = wfhService.getAllWfhRequests(date, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<WfhResponse>> approveWfhRequest(
            @PathVariable Long id,
            @RequestBody(required = false) WfhApprovalRequest approvalRequest
    ) {
        WfhResponse response = wfhService.approveWfhRequest(id, approvalRequest);
        return ResponseEntity.ok(ApiResponse.success("WFH request approved successfully", response));
    }

    @PatchMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<WfhResponse>> rejectWfhRequest(
            @PathVariable Long id,
            @RequestBody(required = false) WfhApprovalRequest approvalRequest
    ) {
        WfhResponse response = wfhService.rejectWfhRequest(id, approvalRequest);
        return ResponseEntity.ok(ApiResponse.success("WFH request rejected", response));
    }

    @PatchMapping("/requests/{id}/cancel")
    public ResponseEntity<ApiResponse<WfhResponse>> cancelWfhRequest(@PathVariable Long id) {
        WfhResponse response = wfhService.cancelWfhRequest(id);
        return ResponseEntity.ok(ApiResponse.success("WFH request cancelled", response));
    }

    @PostMapping("/location")
    public ResponseEntity<ApiResponse<Void>> recordLocation(@Valid @RequestBody WfhLocationRequest locationRequest) {
        wfhService.recordLocation(locationRequest);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", null));
    }

    @GetMapping("/location/latest/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<WfhActiveEmployeeDto>> getLatestLocation(@PathVariable Long employeeId) {
        WfhActiveEmployeeDto dto = wfhService.getLatestLocation(employeeId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/location/history/{employeeId}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<WfhActiveEmployeeDto>>> getLocationHistory(@PathVariable Long employeeId) {
        List<WfhActiveEmployeeDto> list = wfhService.getLocationHistory(employeeId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
