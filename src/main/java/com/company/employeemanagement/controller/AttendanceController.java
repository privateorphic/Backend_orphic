package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.attendance.*;
import com.company.employeemanagement.exception.ApiResponse;
import com.company.employeemanagement.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/attendance", "/api/v1/attendance"})
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Attendance", description = "Employee Daily Attendance & Task Tracking APIs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @Operation(summary = "Perform morning check-in with GPS location")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.checkIn(request), "Morning check-in successful. Morning tasks snapshot recorded."));
    }

    @PostMapping("/wfh/check-in")
    @Operation(summary = "Perform WFH morning check-in with location verification")
    public ResponseEntity<ApiResponse<AttendanceResponse>> wfhCheckIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.wfhCheckIn(request), "WFH Check-in successful. Workday active."));
    }

    @PostMapping("/check-out")
    @Operation(summary = "Perform evening check-out")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(@RequestBody(required = false) CheckOutRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.checkOut(request), "Evening check-out successful. Daily task summary finalized."));
    }

    @PostMapping("/end-work-day")
    @Operation(summary = "Explicitly end WFH or office workday")
    public ResponseEntity<ApiResponse<AttendanceResponse>> endWorkDay(@RequestBody(required = false) CheckOutRequest request) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.endWorkDay(request), "Workday completed successfully."));
    }

    @GetMapping("/today")
    @Operation(summary = "Get current employee's attendance status & task summary for today")
    public ResponseEntity<ApiResponse<DailyAttendanceSummaryDto>> getTodayAttendance() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getTodayAttendance()));
    }
}
