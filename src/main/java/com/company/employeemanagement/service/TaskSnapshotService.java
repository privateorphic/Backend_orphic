package com.company.employeemanagement.service;

import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.DailyTaskSnapshotRepository;
import com.company.employeemanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        LocalDate attendanceDate = attendance.getAttendanceDate() != null ? attendance.getAttendanceDate() : LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        List<DailyTaskSnapshot> snapshots = new ArrayList<>();

        for (Task t : currentTasks) {
            // In the morning, only capture active/pending tasks (TODO, IN_PROGRESS) or tasks starting today.
            // Exclude tasks already completed on previous days.
            if (snapshotType == SnapshotType.MORNING) {
                if (t.getStatus() == TaskStatus.COMPLETED) {
                    boolean completedBeforeToday = (t.getCompletedAt() != null && t.getCompletedAt().toLocalDate().isBefore(attendanceDate))
                            || (t.getCompletedAt() == null && t.getUpdatedAt() != null && t.getUpdatedAt().toLocalDate().isBefore(attendanceDate));
                    if (completedBeforeToday) {
                        continue;
                    }
                }
            }

            // In the evening (checkout), ONLY capture:
            // - Active tasks (TODO, IN_PROGRESS)
            // - Tasks that were COMPLETED TODAY (on this attendanceDate)
            // Exclude tasks completed on previous days so they are not re-added at checkout time.
            if (snapshotType == SnapshotType.EVENING) {
                if (t.getStatus() == TaskStatus.COMPLETED) {
                    boolean completedToday = (t.getCompletedAt() != null && t.getCompletedAt().toLocalDate().isEqual(attendanceDate))
                            || (t.getCompletedAt() == null && t.getUpdatedAt() != null && t.getUpdatedAt().toLocalDate().isEqual(attendanceDate));
                    if (!completedToday) {
                        continue;
                    }
                }
            }

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
