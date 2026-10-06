package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.entity.WfhLocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WfhLocationHistoryRepository extends JpaRepository<WfhLocationHistory, Long> {

    List<WfhLocationHistory> findByEmployeeOrderByCapturedAtDesc(User employee);

    @Query("SELECT w FROM WfhLocationHistory w WHERE w.employee.id = :employeeId ORDER BY w.capturedAt DESC LIMIT 1")
    Optional<WfhLocationHistory> findLatestByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT w FROM WfhLocationHistory w WHERE w.employee.id = :employeeId ORDER BY w.capturedAt DESC")
    List<WfhLocationHistory> findHistoryByEmployeeId(@Param("employeeId") Long employeeId);
}
