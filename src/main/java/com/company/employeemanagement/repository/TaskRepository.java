package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.Task;
import com.company.employeemanagement.entity.TaskPriority;
import com.company.employeemanagement.entity.TaskStatus;
import com.company.employeemanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByAssignedTo(User user);
    Page<Task> findByAssignedTo(User user, Pageable pageable);

    List<Task> findByAssignedToAndStatus(User user, TaskStatus status);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(TaskPriority priority);

    Page<Task> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    long countByStatus(TaskStatus status);
    long countByAssignedToAndStatus(User user, TaskStatus status);
    long countByAssignedTo(User user);

    @Query("SELECT t FROM Task t WHERE t.status != 'COMPLETED' AND t.status != 'CANCELLED' AND t.deadline < :today")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> countByPriorityGrouped();

    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.status = 'TODO' OR t.status = 'IN_PROGRESS' ORDER BY t.deadline ASC")
    List<Task> findPendingTasksForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId AND t.status = :status")
    long countByAssignedToIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);
}
