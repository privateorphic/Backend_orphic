package com.company.employeemanagement.dto.task;

import com.company.employeemanagement.entity.TaskPriority;
import com.company.employeemanagement.entity.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 300)
    private String title;

    private String description;

    private Long assignedToId;

    private Long departmentId;

    private TaskPriority priority;

    private TaskStatus status;

    @Min(0) @Max(100)
    private Integer progressPercentage;

    private LocalDate startDate;

    private LocalDate deadline;
}
