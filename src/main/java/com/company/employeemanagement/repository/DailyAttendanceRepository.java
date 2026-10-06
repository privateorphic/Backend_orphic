package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.DailyAttendance;
import com.company.employeemanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, Long> {

    Optional<DailyAttendance> findByEmployeeAndAttendanceDate(User employee, LocalDate attendanceDate);

    Optional<DailyAttendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    Page<DailyAttendance> findByAttendanceDate(LocalDate attendanceDate, Pageable pageable);

    List<DailyAttendance> findByAttendanceDate(LocalDate attendanceDate);

    Page<DailyAttendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId, Pageable pageable);

    Page<DailyAttendance> findByEmployeeId(Long employeeId, Pageable pageable);

    List<DailyAttendance> findByEmployeeId(Long employeeId);
}
