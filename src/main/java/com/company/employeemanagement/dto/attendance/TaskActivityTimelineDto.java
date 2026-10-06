package com.company.employeemanagement.dto.attendance;

import com.company.employeemanagement.entity.ActivityActionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskActivityTimelineDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private ActivityActionType actionType;
    private String oldStatus;
    private String newStatus;
    private Integer oldProgress;
    private Integer newProgress;
    private String description;
    private LocalDateTime createdAt;
}
