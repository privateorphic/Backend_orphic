package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.employee.CreateEmployeeRequest;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.employee.UpdateEmployeeRequest;
import com.company.employeemanagement.dto.report.AdminDashboardResponse;
import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import com.company.employeemanagement.dto.task.CreateTaskRequest;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.company.employeemanagement.dto.task.UpdateTaskRequest;
import com.company.employeemanagement.dto.hr.DepartmentRequest;
import com.company.employeemanagement.dto.hr.DepartmentResponse;
import com.company.employeemanagement.entity.UserStatus;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only APIs")
public class AdminController {

    private final UserService userService;
    private final TaskService taskService;
    private final DashboardService dashboardService;
    private final LoginActivityService loginActivityService;
    private final DepartmentService departmentService;

    // ===== DASHBOARD =====
    @GetMapping("/dashboard")
    @Operation(summary = "Admin dashboard with real-time stats")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminDashboard()));
    }

    // ===== EMPLOYEES =====
    @PostMapping("/employees")
    @Operation(summary = "Create new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(userService.createEmployee(request), "Employee created successfully"));
    }

    @GetMapping("/employees")
    @Operation(summary = "List all employees")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(userService.getAllEmployees(pageable)));
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getEmployee(id)));
    }

    @GetMapping("/employees/by-employee-id/{employeeId}")
    @Operation(summary = "Get employee by employee ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getEmployeeByEmployeeId(employeeId)));
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable Long id,
                                                                         @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateEmployee(id, request), "Employee updated"));
    }

    @PatchMapping("/employees/{id}/status")
    @Operation(summary = "Change employee status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable Long id,
                                                           @RequestParam UserStatus status) {
        userService.changeEmployeeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Status updated"));
    }

    @DeleteMapping("/employees/{id}")
    @Operation(summary = "Deactivate (soft-delete) employee")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        userService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee deactivated"));
    }

    // ===== TASKS =====
    @PostMapping("/tasks")
    @Operation(summary = "Create a new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(taskService.createTask(request), "Task created successfully"));
    }

    @GetMapping("/tasks")
    @Operation(summary = "List all tasks")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(taskService.getAllTasks(pageable)));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTask(id)));
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable Long id,
                                                                 @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(id, request), "Task updated"));
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Cancel task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Task cancelled"));
    }

    // ===== LOGIN ACTIVITY =====
    @GetMapping("/login-activity")
    @Operation(summary = "All login activity with pagination")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> getLoginActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getAllLoginActivity(pageable)));
    }

    @GetMapping("/login-activity/today")
    @Operation(summary = "Today's login activity")
    public ResponseEntity<ApiResponse<List<LoginActivityResponse>>> getTodayActivity() {
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getTodayLoginActivity()));
    }

    @GetMapping("/login-activity/employee/{employeeId}")
    @Operation(summary = "Login activity for a specific employee")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> getEmployeeActivity(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getEmployeeLoginActivity(employeeId, pageable)));
    }

    @GetMapping("/login-activity/range")
    @Operation(summary = "Login activity within a date range")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> getActivityByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getLoginActivityByDateRange(start, end, pageable)));
    }

    // ===== DEPARTMENTS =====
    @PostMapping("/departments")
    @Operation(summary = "Create a department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(departmentService.createDepartment(request), "Department created"));
    }

    @GetMapping("/departments")
    @Operation(summary = "List all departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getAllDepartments()));
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(@PathVariable Long id,
                                                                              @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.updateDepartment(id, request)));
    }
}
