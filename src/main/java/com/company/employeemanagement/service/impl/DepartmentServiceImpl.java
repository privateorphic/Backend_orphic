package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.hr.DepartmentRequest;
import com.company.employeemanagement.dto.hr.DepartmentResponse;
import com.company.employeemanagement.entity.Department;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.DepartmentRepository;
import com.company.employeemanagement.repository.UserRepository;
import com.company.employeemanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Department already exists: " + request.getName());
        }
        Department dept = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .headOfDepartment(request.getHeadOfDepartment())
                .isActive(true)
                .build();
        return mapToResponse(departmentRepository.save(dept));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartment(Long id) {
        return mapToResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id)));
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        dept.setHeadOfDepartment(request.getHeadOfDepartment());
        return mapToResponse(departmentRepository.save(dept));
    }

    private DepartmentResponse mapToResponse(Department dept) {
        long empCount = userRepository.findByDepartmentId(dept.getId()).size();
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .headOfDepartment(dept.getHeadOfDepartment())
                .isActive(dept.getIsActive())
                .employeeCount(empCount)
                .createdAt(dept.getCreatedAt())
                .build();
    }
}
