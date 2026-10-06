package com.company.employeemanagement.dto.auth;

import com.company.employeemanagement.entity.Role;

public class UserInfo {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private Role role;
    private String departmentName;
    private String jobTitle;
    private String status;

    public UserInfo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static UserInfoBuilder builder() { return new UserInfoBuilder(); }

    public static class UserInfoBuilder {
        private final UserInfo u = new UserInfo();

        public UserInfoBuilder id(Long v) { u.id = v; return this; }
        public UserInfoBuilder employeeId(String v) { u.employeeId = v; return this; }
        public UserInfoBuilder name(String v) { u.name = v; return this; }
        public UserInfoBuilder email(String v) { u.email = v; return this; }
        public UserInfoBuilder role(Role v) { u.role = v; return this; }
        public UserInfoBuilder departmentName(String v) { u.departmentName = v; return this; }
        public UserInfoBuilder jobTitle(String v) { u.jobTitle = v; return this; }
        public UserInfoBuilder status(String v) { u.status = v; return this; }

        public UserInfo build() { return u; }
    }
}
