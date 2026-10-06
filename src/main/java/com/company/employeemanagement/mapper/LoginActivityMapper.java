package com.company.employeemanagement.mapper;

import com.company.employeemanagement.dto.attendance.LoginActivityResponse;
import com.company.employeemanagement.entity.LoginActivity;
import org.springframework.stereotype.Component;

@Component
public class LoginActivityMapper {

    public LoginActivityResponse toLoginActivityResponse(LoginActivity la) {
        if (la == null) return null;
        return LoginActivityResponse.builder()
                .id(la.getId())
                .userId(la.getUser() != null ? la.getUser().getId() : null)
                .employeeId(la.getUser() != null ? la.getUser().getEmployeeId() : null)
                .employeeName(la.getUser() != null ? la.getUser().getName() : null)
                .departmentName(la.getUser() != null && la.getUser().getDepartment() != null
                        ? la.getUser().getDepartment().getName() : null)
                .loginDate(la.getLoginDate())
                .loginTime(la.getLoginTime())
                .logoutTime(la.getLogoutTime())
                .sessionDuration(la.getSessionDuration())
                .status(la.getStatus())
                .ipAddress(la.getIpAddress())
                .isOfficeLocation(la.getIsOfficeLocation())
                .latitude(la.getLatitude())
                .longitude(la.getLongitude())
                .locationName(la.getLocationName())
                .build();
    }
}
