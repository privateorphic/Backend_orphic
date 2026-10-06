package com.company.employeemanagement.service;

import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.TaskActivityHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskActivityService {

    private final TaskActivityHistoryRepository taskActivityHistoryRepository;

    @Transactional
    public TaskActivityHistory logActivity(
            Task task,
            User employee,
            DailyAttendance attendance,
            ActivityActionType actionType,
            String oldStatus,
            String newStatus,
            Integer oldProgress,
            Integer newProgress,
            String description) {

        TaskActivityHistory history = new TaskActivityHistory();
        history.setTask(task);
        history.setEmployee(employee);
        history.setAttendance(attendance);
        history.setActionType(actionType);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setOldProgress(oldProgress);
        history.setNewProgress(newProgress);
        history.setDescription(description);

        return taskActivityHistoryRepository.save(history);
    }
}
