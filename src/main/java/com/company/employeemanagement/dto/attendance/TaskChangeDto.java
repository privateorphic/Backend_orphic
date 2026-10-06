package com.company.employeemanagement.dto.attendance;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskChangeDto {
    private List<TaskSnapshotDto> addedTasks;
    private List<TaskSnapshotDto> updatedTasks;
    private List<TaskSnapshotDto> completedTasks;
}
