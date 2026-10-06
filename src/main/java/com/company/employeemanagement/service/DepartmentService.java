package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.hr.DepartmentRequest;
import com.company.employeemanagement.dto.hr.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartment(Long id);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
}
