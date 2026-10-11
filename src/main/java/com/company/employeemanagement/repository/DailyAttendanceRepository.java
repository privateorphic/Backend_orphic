package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.DailyAttendance;
import com.company.employeemanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, Long> {


    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    Optional<DailyAttendance> findByEmployeeAndAttendanceDate(User employee, LocalDate attendanceDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    Optional<DailyAttendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    Page<DailyAttendance> findByAttendanceDate(LocalDate attendanceDate, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    List<DailyAttendance> findByAttendanceDate(LocalDate attendanceDate);

    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    Page<DailyAttendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    Page<DailyAttendance> findByEmployeeId(Long employeeId, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "employee.department", "officeLocation"})
    List<DailyAttendance> findByEmployeeId(Long employeeId);
}
