package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.ApiResponse;
import com.company.employeemanagement.dto.wfh.WfhActiveEmployeeDto;
import com.company.employeemanagement.service.WfhService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/admin/wfh", "/api/v1/admin/wfh", "/api/hr/wfh", "/api/v1/hr/wfh"})
@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
@RequiredArgsConstructor
public class AdminWfhController {

    private final WfhService wfhService;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<WfhActiveEmployeeDto>>> getActiveWfhEmployees() {
        List<WfhActiveEmployeeDto> activeList = wfhService.getActiveWfhEmployees();
        return ResponseEntity.ok(ApiResponse.success(activeList));
    }

    @GetMapping("/location")
    public ResponseEntity<ApiResponse<List<WfhActiveEmployeeDto>>> getWfhLocations() {
        List<WfhActiveEmployeeDto> activeList = wfhService.getActiveWfhEmployees();
        return ResponseEntity.ok(ApiResponse.success(activeList));
    }
}
