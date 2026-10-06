package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.dailywork.DailyWorkRequest;
import com.company.employeemanagement.dto.dailywork.DailyWorkResponse;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.employee.UpdateEmployeeRequest;
import com.company.employeemanagement.dto.leave.LeaveApplicationRequest;
import com.company.employeemanagement.dto.leave.LeaveResponse;
import com.company.employeemanagement.dto.notification.NotificationResponse;
import com.company.employeemanagement.dto.report.EmployeeDashboardResponse;
import com.company.employeemanagement.dto.task.CreateTaskRequest;
import com.company.employeemanagement.dto.task.EmployeeTaskUpdateRequest;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.company.employeemanagement.exception.ApiResponse;
import com.company.employeemanagement.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Employee", description = "Employee Self-Service APIs")
public class EmployeeController {

    private final UserService userService;
    private final TaskService taskService;
    private final DailyWorkService dailyWorkService;
    private final LeaveService leaveService;
    private final DashboardService dashboardService;
    private final NotificationService notificationService;
    private final LoginActivityService loginActivityService;

    // ===== MY PROFILE =====
    @GetMapping("/profile")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my profile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateProfile(@RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateMyProfile(request), "Profile updated"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change my password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody com.company.employeemanagement.dto.employee.ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    // ===== DASHBOARD =====
    @GetMapping("/dashboard")
    @Operation(summary = "My employee dashboard")
    public ResponseEntity<ApiResponse<EmployeeDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getEmployeeDashboard()));
    }

    // ===== TASKS =====
    @GetMapping("/tasks")
    @Operation(summary = "My assigned tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasks() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getMyTasks()));
    }

    @PostMapping("/tasks")
    @Operation(summary = "Create a new task for myself")
    public ResponseEntity<ApiResponse<TaskResponse>> createMyTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(taskService.createEmployeeTask(request), "Task created successfully"));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get a specific task assigned to me")
    public ResponseEntity<ApiResponse<TaskResponse>> getMyTask(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getMyTask(id)));
    }

    @PatchMapping("/tasks/{id}")
    @Operation(summary = "Update my task status/progress")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable Long id,
                                                                 @RequestBody EmployeeTaskUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateMyTaskProgress(id, request), "Task updated"));
    }

    // ===== DAILY WORK =====
    @PostMapping("/daily-work")
    @Operation(summary = "Submit daily work log")
    public ResponseEntity<ApiResponse<DailyWorkResponse>> submitWork(@Valid @RequestBody DailyWorkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(dailyWorkService.submitDailyWork(request), "Daily work submitted"));
    }

    @GetMapping("/daily-work")
    @Operation(summary = "My daily work logs")
    public ResponseEntity<ApiResponse<List<DailyWorkResponse>>> getMyDailyWork() {
        return ResponseEntity.ok(ApiResponse.success(dailyWorkService.getMyDailyWork()));
    }

    @GetMapping("/work-history")
    @Operation(summary = "My work history (paginated)")
    public ResponseEntity<ApiResponse<Page<DailyWorkResponse>>> getWorkHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("workDate").descending());
        return ResponseEntity.ok(ApiResponse.success(dailyWorkService.getMyWorkHistory(pageable)));
    }

    // ===== LEAVES =====
    @PostMapping("/leaves")
    @Operation(summary = "Apply for leave")
    public ResponseEntity<ApiResponse<LeaveResponse>> applyLeave(@Valid @RequestBody LeaveApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(leaveService.applyLeave(request), "Leave applied successfully"));
    }

    @GetMapping("/leaves")
    @Operation(summary = "My leave requests")
    public ResponseEntity<ApiResponse<Page<LeaveResponse>>> getMyLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(leaveService.getMyLeaves(pageable)));
    }

    @PatchMapping("/leaves/{id}/cancel")
    @Operation(summary = "Cancel my leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> cancelLeave(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.cancelLeave(id), "Leave cancelled"));
    }

    // ===== LOGIN ACTIVITY =====
    @GetMapping("/login-activity")
    @Operation(summary = "My login activity/history")
    public ResponseEntity<ApiResponse<Page<com.company.employeemanagement.dto.attendance.LoginActivityResponse>>> getMyLoginActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getMyLoginActivity(pageable)));
    }

    // ===== NOTIFICATIONS =====
    @GetMapping("/notifications")
    @Operation(summary = "My notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(notificationService.getMyNotifications(pageable)));
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "Unread notification count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount()));
    }

    @PatchMapping("/notifications/mark-all-read")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        notificationService.markAllRead();
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    @PatchMapping("/notifications/{id}/read")
    @Operation(summary = "Mark specific notification as read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }
}
