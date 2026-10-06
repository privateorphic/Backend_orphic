package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.ManagerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerFeedbackRepository extends JpaRepository<ManagerFeedback, Long> {

    List<ManagerFeedback> findByEmployeeId(Long employeeId);

    List<ManagerFeedback> findByManagerId(Long managerId);

    List<ManagerFeedback> findByTaskId(Long taskId);
}
