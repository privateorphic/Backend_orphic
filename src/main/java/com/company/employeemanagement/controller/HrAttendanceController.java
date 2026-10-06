package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.attendance.*;
import com.company.employeemanagement.exception.ApiResponse;
import com.company.employeemanagement.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/hr/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
@Tag(name = "HR Attendance", description = "HR Employee Daily Attendance Portal")
public class HrAttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @Operation(summary = "HR view daily attendance list for all employees")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getHrAttendanceList(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("attendanceDate").descending());
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAdminAttendanceList(date, pageable)));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "HR view employee daily activity response")
    public ResponseEntity<ApiResponse<EmployeeDailyActivityResponse>> getEmployeeDailyActivity(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getEmployeeDailyActivity(employeeId, date)));
    }
}
