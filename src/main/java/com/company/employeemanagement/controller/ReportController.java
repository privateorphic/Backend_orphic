package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import com.company.employeemanagement.dto.dailywork.DailyWorkResponse;
import com.company.employeemanagement.dto.leave.LeaveResponse;
import com.company.employeemanagement.exception.ApiResponse;
import com.company.employeemanagement.service.*;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Admin and HR Reports")
public class ReportController {

    private final LoginActivityService loginActivityService;
    private final LeaveService leaveService;
    private final DailyWorkService dailyWorkService;

    // ===== ADMIN REPORTS =====
    @GetMapping("/admin/reports/daily")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Daily attendance report for today")
    public ResponseEntity<ApiResponse<?>> dailyReport() {
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getTodayLoginActivity(), "Daily report"));
    }

    @GetMapping("/admin/reports/weekly")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Weekly attendance report")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> weeklyReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                loginActivityService.getLoginActivityByDateRange(weekStart, today, pageable), "Weekly report"));
    }

    @GetMapping("/admin/reports/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Monthly attendance report")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> monthlyReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                loginActivityService.getLoginActivityByDateRange(monthStart, today, pageable), "Monthly report"));
    }

    // ===== HR REPORTS =====
    @GetMapping("/hr/reports/attendance")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "HR attendance report for date range")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> hrAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("loginDate").descending());
        return ResponseEntity.ok(ApiResponse.success(
                loginActivityService.getLoginActivityByDateRange(start, end, pageable), "Attendance report"));
    }

    @GetMapping("/hr/reports/leave")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "HR leave report")
    public ResponseEntity<ApiResponse<Page<LeaveResponse>>> hrLeaveReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(leaveService.getAllLeaves(pageable), "Leave report"));
    }

    @GetMapping("/hr/reports/daily-work")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @Operation(summary = "Daily work submissions report")
    public ResponseEntity<ApiResponse<Page<DailyWorkResponse>>> hrDailyWorkReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("workDate").descending());
        return ResponseEntity.ok(ApiResponse.success(dailyWorkService.getAllDailyWork(pageable), "Daily work report"));
    }
}
