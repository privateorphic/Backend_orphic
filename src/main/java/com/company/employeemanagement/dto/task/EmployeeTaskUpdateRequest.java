package com.company.employeemanagement.dto.task;

import com.company.employeemanagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeTaskUpdateRequest {
    private TaskStatus status;
    private Integer progressPercentage;
    private String workUpdate;

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getWorkUpdate() { return workUpdate; }
    public void setWorkUpdate(String workUpdate) { this.workUpdate = workUpdate; }
}
