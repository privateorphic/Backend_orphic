package com.company.employeemanagement.mapper;

import com.company.employeemanagement.dto.auth.UserInfo;
import com.company.employeemanagement.dto.employee.EmployeeResponse;
import com.company.employeemanagement.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public EmployeeResponse toEmployeeResponse(User user) {
        if (user == null) return null;
        return EmployeeResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .jobTitle(user.getJobTitle())
                .joiningDate(user.getJoiningDate())
                .employmentType(user.getEmploymentType())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserInfo toUserInfo(User user) {
        if (user == null) return null;
        return UserInfo.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .jobTitle(user.getJobTitle())
                .status(user.getStatus().name())
                .build();
    }
}
