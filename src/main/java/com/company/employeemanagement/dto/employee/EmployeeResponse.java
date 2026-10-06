package com.company.employeemanagement.dto.employee;

import com.company.employeemanagement.entity.EmploymentType;
import com.company.employeemanagement.entity.Role;
import com.company.employeemanagement.entity.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Long departmentId;
    private String departmentName;
    private String jobTitle;
    private LocalDate joiningDate;
    private EmploymentType employmentType;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static EmployeeResponseBuilder builder() { return new EmployeeResponseBuilder(); }

    public static class EmployeeResponseBuilder {
        private final EmployeeResponse r = new EmployeeResponse();

        public EmployeeResponseBuilder id(Long v) { r.id = v; return this; }
        public EmployeeResponseBuilder employeeId(String v) { r.employeeId = v; return this; }
        public EmployeeResponseBuilder name(String v) { r.name = v; return this; }
        public EmployeeResponseBuilder email(String v) { r.email = v; return this; }
        public EmployeeResponseBuilder phone(String v) { r.phone = v; return this; }
        public EmployeeResponseBuilder role(Role v) { r.role = v; return this; }
        public EmployeeResponseBuilder departmentId(Long v) { r.departmentId = v; return this; }
        public EmployeeResponseBuilder departmentName(String v) { r.departmentName = v; return this; }
        public EmployeeResponseBuilder jobTitle(String v) { r.jobTitle = v; return this; }
        public EmployeeResponseBuilder joiningDate(LocalDate v) { r.joiningDate = v; return this; }
        public EmployeeResponseBuilder employmentType(EmploymentType v) { r.employmentType = v; return this; }
        public EmployeeResponseBuilder status(UserStatus v) { r.status = v; return this; }
        public EmployeeResponseBuilder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public EmployeeResponseBuilder updatedAt(LocalDateTime v) { r.updatedAt = v; return this; }

        public EmployeeResponse build() { return r; }
    }
}
