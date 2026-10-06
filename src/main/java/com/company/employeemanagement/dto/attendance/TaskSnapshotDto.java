package com.company.employeemanagement.dto.attendance;

import com.company.employeemanagement.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskSnapshotDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private TaskStatus taskStatus;
    private Integer taskProgress;
    private LocalDateTime snapshotTime;
}
