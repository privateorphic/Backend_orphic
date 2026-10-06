package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.LeaveRequest;
import com.company.employeemanagement.entity.LeaveStatus;
import com.company.employeemanagement.entity.LeaveType;
import com.company.employeemanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUserOrderByCreatedAtDesc(User user);
    Page<LeaveRequest> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<LeaveRequest> findByStatus(LeaveStatus status, Pageable pageable);
    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByUserAndStatus(User user, LeaveStatus status);

    long countByStatus(LeaveStatus status);
    long countByUserAndStatus(User user, LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.startDate <= :date AND lr.endDate >= :date AND lr.status = 'APPROVED'")
    List<LeaveRequest> findActiveLeaveOnDate(@Param("date") LocalDate date);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user = :user AND lr.startDate <= :date AND lr.endDate >= :date AND lr.status = 'APPROVED' AND lr.leaveType = com.company.employeemanagement.entity.LeaveType.WORK_FROM_HOME")
    List<LeaveRequest> findApprovedWfhRequestOnDate(@Param("user") User user, @Param("date") LocalDate date);

    List<LeaveRequest> findByUserAndLeaveType(User user, LeaveType leaveType);
    List<LeaveRequest> findByLeaveTypeOrderByCreatedAtDesc(LeaveType leaveType);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user = :user AND lr.startDate <= :date AND lr.endDate >= :date AND lr.status = 'APPROVED'")
    List<LeaveRequest> findApprovedLeaveOnDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT lr.leaveType, COUNT(lr) FROM LeaveRequest lr GROUP BY lr.leaveType")
    List<Object[]> countByLeaveType();

    @Query("SELECT COUNT(DISTINCT lr.user) FROM LeaveRequest lr WHERE lr.startDate <= :date AND lr.endDate >= :date AND lr.status = 'APPROVED'")
    long countEmployeesOnLeaveOnDate(@Param("date") LocalDate date);
}
