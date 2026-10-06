package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.DailyAttendance;
import com.company.employeemanagement.entity.DailyTaskSnapshot;
import com.company.employeemanagement.entity.SnapshotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyTaskSnapshotRepository extends JpaRepository<DailyTaskSnapshot, Long> {

    List<DailyTaskSnapshot> findByAttendanceAndSnapshotType(DailyAttendance attendance, SnapshotType snapshotType);

    List<DailyTaskSnapshot> findByAttendanceIdAndSnapshotType(Long attendanceId, SnapshotType snapshotType);

    List<DailyTaskSnapshot> findByAttendanceId(Long attendanceId);

    void deleteByAttendanceAndSnapshotType(DailyAttendance attendance, SnapshotType snapshotType);
}
