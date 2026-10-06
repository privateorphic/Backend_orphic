package com.company.employeemanagement.dto.hr;

import java.time.LocalDateTime;

public class DepartmentResponse {
    private Long id;
    private String name;
    private String description;
    private String headOfDepartment;
    private Boolean isActive;
    private long employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DepartmentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHeadOfDepartment() { return headOfDepartment; }
    public void setHeadOfDepartment(String headOfDepartment) { this.headOfDepartment = headOfDepartment; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public long getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(long employeeCount) { this.employeeCount = employeeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static DepartmentResponseBuilder builder() { return new DepartmentResponseBuilder(); }

    public static class DepartmentResponseBuilder {
        private final DepartmentResponse r = new DepartmentResponse();

        public DepartmentResponseBuilder id(Long v) { r.id = v; return this; }
        public DepartmentResponseBuilder name(String v) { r.name = v; return this; }
        public DepartmentResponseBuilder description(String v) { r.description = v; return this; }
        public DepartmentResponseBuilder headOfDepartment(String v) { r.headOfDepartment = v; return this; }
        public DepartmentResponseBuilder isActive(Boolean v) { r.isActive = v; return this; }
        public DepartmentResponseBuilder employeeCount(long v) { r.employeeCount = v; return this; }
        public DepartmentResponseBuilder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public DepartmentResponseBuilder updatedAt(LocalDateTime v) { r.updatedAt = v; return this; }

        public DepartmentResponse build() { return r; }
    }
}
