package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.DailyAttendance;
import com.company.employeemanagement.entity.TaskActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskActivityHistoryRepository extends JpaRepository<TaskActivityHistory, Long> {

    List<TaskActivityHistory> findByAttendanceOrderByCreatedAtAsc(DailyAttendance attendance);

    List<TaskActivityHistory> findByAttendanceIdOrderByCreatedAtAsc(Long attendanceId);

    List<TaskActivityHistory> findByEmployeeIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long employeeId, LocalDateTime startOfDay, LocalDateTime endOfDay
    );

    List<TaskActivityHistory> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
