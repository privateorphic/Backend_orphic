package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.employee.CreateEmployeeRequest;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.dto.employee.UpdateEmployeeRequest;
import com.company.employeemanagement.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    EmployeeResponse createEmployee(CreateEmployeeRequest request);
    EmployeeResponse getEmployee(Long id);
    EmployeeResponse getEmployeeByEmployeeId(String employeeId);
    Page<EmployeeResponse> getAllEmployees(Pageable pageable);
    List<EmployeeResponse> getEmployeesByDepartment(Long departmentId);
    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);
    void changeEmployeeStatus(Long id, UserStatus status);
    void deleteEmployee(Long id);
    EmployeeResponse getMyProfile();
    EmployeeResponse updateMyProfile(UpdateEmployeeRequest request);
    void changePassword(com.company.employeemanagement.dto.employee.ChangePasswordRequest request);
}
