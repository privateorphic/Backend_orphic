package com.company.employeemanagement.dto.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.company.employeemanagement.entity.LoginStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class LoginActivityResponse {
    private Long id;
    private Long userId;
    private String employeeId;
    private String employeeName;
    private String departmentName;
    private LocalDate loginDate;
    @JsonFormat(pattern = "hh:mm:ss a")
    private LocalTime loginTime;
    @JsonFormat(pattern = "hh:mm:ss a")
    private LocalTime logoutTime;
    private String sessionDuration;
    private LoginStatus status;
    private String ipAddress;
    private Boolean isOfficeLocation;
    private Double latitude;
    private Double longitude;
    private String locationName;

    public LoginActivityResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public LocalDate getLoginDate() { return loginDate; }
    public void setLoginDate(LocalDate loginDate) { this.loginDate = loginDate; }

    public LocalTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalTime loginTime) { this.loginTime = loginTime; }

    public LocalTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalTime logoutTime) { this.logoutTime = logoutTime; }

    public String getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(String sessionDuration) { this.sessionDuration = sessionDuration; }

    public LoginStatus getStatus() { return status; }
    public void setStatus(LoginStatus status) { this.status = status; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Boolean getIsOfficeLocation() { return isOfficeLocation; }
    public void setIsOfficeLocation(Boolean isOfficeLocation) { this.isOfficeLocation = isOfficeLocation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public static LoginActivityResponseBuilder builder() { return new LoginActivityResponseBuilder(); }

    public static class LoginActivityResponseBuilder {
        private final LoginActivityResponse r = new LoginActivityResponse();

        public LoginActivityResponseBuilder id(Long v) { r.id = v; return this; }
        public LoginActivityResponseBuilder userId(Long v) { r.userId = v; return this; }
        public LoginActivityResponseBuilder employeeId(String v) { r.employeeId = v; return this; }
        public LoginActivityResponseBuilder employeeName(String v) { r.employeeName = v; return this; }
        public LoginActivityResponseBuilder departmentName(String v) { r.departmentName = v; return this; }
        public LoginActivityResponseBuilder loginDate(LocalDate v) { r.loginDate = v; return this; }
        public LoginActivityResponseBuilder loginTime(LocalTime v) { r.loginTime = v; return this; }
        public LoginActivityResponseBuilder logoutTime(LocalTime v) { r.logoutTime = v; return this; }
        public LoginActivityResponseBuilder sessionDuration(String v) { r.sessionDuration = v; return this; }
        public LoginActivityResponseBuilder status(LoginStatus v) { r.status = v; return this; }
        public LoginActivityResponseBuilder ipAddress(String v) { r.ipAddress = v; return this; }
        public LoginActivityResponseBuilder isOfficeLocation(Boolean v) { r.isOfficeLocation = v; return this; }
        public LoginActivityResponseBuilder latitude(Double v) { r.latitude = v; return this; }
        public LoginActivityResponseBuilder longitude(Double v) { r.longitude = v; return this; }
        public LoginActivityResponseBuilder locationName(String v) { r.locationName = v; return this; }

        public LoginActivityResponse build() { return r; }
    }
}
