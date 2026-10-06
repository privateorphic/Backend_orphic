package com.company.employeemanagement.service;

import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.DailyTaskSnapshotRepository;
import com.company.employeemanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskSnapshotService {

    private final TaskRepository taskRepository;
    private final DailyTaskSnapshotRepository dailyTaskSnapshotRepository;

    @Transactional
    public List<DailyTaskSnapshot> captureTaskSnapshot(User employee, DailyAttendance attendance, SnapshotType snapshotType) {
        // If snapshot of this type already exists for this attendance, don't overwrite morning snapshot
        if (snapshotType == SnapshotType.MORNING) {
            List<DailyTaskSnapshot> existingMorning = dailyTaskSnapshotRepository.findByAttendanceAndSnapshotType(attendance, SnapshotType.MORNING);
            if (!existingMorning.isEmpty()) {
                return existingMorning;
            }
        } else if (snapshotType == SnapshotType.EVENING) {
            // For evening, clear previous evening snapshot if re-checking out or updating
            dailyTaskSnapshotRepository.deleteByAttendanceAndSnapshotType(attendance, SnapshotType.EVENING);
        }

        List<Task> currentTasks = taskRepository.findByAssignedTo(employee);
        LocalDateTime now = LocalDateTime.now();
        List<DailyTaskSnapshot> snapshots = new ArrayList<>();

        for (Task t : currentTasks) {
            DailyTaskSnapshot snapshot = new DailyTaskSnapshot();
            snapshot.setEmployee(employee);
            snapshot.setAttendance(attendance);
            snapshot.setSnapshotType(snapshotType);
            snapshot.setTaskId(t.getId());
            snapshot.setTaskTitle(t.getTitle());
            snapshot.setTaskStatus(t.getStatus());
            snapshot.setTaskProgress(t.getProgressPercentage() != null ? t.getProgressPercentage() : 0);
            snapshot.setSnapshotTime(now);
            snapshots.add(snapshot);
        }

        return dailyTaskSnapshotRepository.saveAll(snapshots);
    }
}
