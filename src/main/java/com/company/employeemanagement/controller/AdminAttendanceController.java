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
@RequestMapping("/api/admin/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin Attendance", description = "Admin & HR Management for Daily Employee Attendance")
public class AdminAttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @Operation(summary = "Get daily attendance list for all employees")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAdminAttendanceList(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("attendanceDate").descending());
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAdminAttendanceList(date, pageable)));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get full daily activity response for an employee on a given date")
    public ResponseEntity<ApiResponse<EmployeeDailyActivityResponse>> getEmployeeDailyActivity(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getEmployeeDailyActivity(employeeId, date)));
    }

    @PostMapping("/{attendanceId}/reopen")
    @Operation(summary = "Reopen an employee's attendance day for task updates")
    public ResponseEntity<ApiResponse<AttendanceResponse>> reopenAttendanceDay(@PathVariable Long attendanceId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.reopenAttendanceDay(attendanceId), "Attendance day reopened successfully."));
    }
}
