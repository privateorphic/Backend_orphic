package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import com.company.employeemanagement.dto.employee.CreateEmployeeRequest;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.employee.UpdateEmployeeRequest;
import com.company.employeemanagement.dto.hr.DepartmentResponse;
import com.company.employeemanagement.dto.leave.LeaveApprovalRequest;
import com.company.employeemanagement.dto.leave.LeaveResponse;
import com.company.employeemanagement.dto.report.HRDashboardResponse;
import com.company.employeemanagement.dto.task.CreateTaskRequest;
import com.company.employeemanagement.dto.task.TaskResponse;
import com.company.employeemanagement.dto.task.UpdateTaskRequest;
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
@RequestMapping("/api/hr")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
@Tag(name = "HR", description = "HR Management APIs")
public class HrController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final LeaveService leaveService;
    private final LoginActivityService loginActivityService;
    private final DepartmentService departmentService;
    private final TaskService taskService;

    // ===== DASHBOARD =====
    @GetMapping("/dashboard")
    @Operation(summary = "HR dashboard")
    public ResponseEntity<ApiResponse<HRDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getHRDashboard()));
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
    @Operation(summary = "Get employee by employee ID code")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getEmployeeByEmployeeId(employeeId)));
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable Long id,
                                                                         @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateEmployee(id, request), "Employee updated"));
    }

    @GetMapping("/employees/department/{departmentId}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getEmployeesByDepartment(departmentId)));
    }

    // ===== LEAVE MANAGEMENT =====
    @GetMapping("/leaves")
    @Operation(summary = "All leave requests")
    public ResponseEntity<ApiResponse<Page<LeaveResponse>>> getAllLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(leaveService.getAllLeaves(pageable)));
    }

    @PostMapping("/leaves/{id}/approve")
    @Operation(summary = "Approve leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> approveLeave(@PathVariable Long id,
                                                                     @RequestBody LeaveApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.approveLeave(id, request), "Leave approved"));
    }

    @PostMapping("/leaves/{id}/reject")
    @Operation(summary = "Reject leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> rejectLeave(@PathVariable Long id,
                                                                    @RequestBody LeaveApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.rejectLeave(id, request), "Leave rejected"));
    }

    // ===== LOGIN ACTIVITY =====
    @GetMapping("/login-activity")
    @Operation(summary = "Login activity list")
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
    @Operation(summary = "Employee-specific login activity")
    public ResponseEntity<ApiResponse<Page<LoginActivityResponse>>> getEmployeeActivity(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(loginActivityService.getEmployeeLoginActivity(employeeId, pageable)));
    }

    // ===== DEPARTMENTS =====
    @GetMapping("/departments")
    @Operation(summary = "List all departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartments() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getAllDepartments()));
    }

    // ===== TASKS =====
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

    @PostMapping("/tasks")
    @Operation(summary = "Create task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(taskService.createTask(request), "Task created successfully"));
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
}
