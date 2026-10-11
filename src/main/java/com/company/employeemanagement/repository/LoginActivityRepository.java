package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.LoginActivity;
import com.company.employeemanagement.entity.LoginStatus;
import com.company.employeemanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginActivityRepository extends JpaRepository<LoginActivity, Long> {

    @EntityGraph(attributePaths = {"user", "user.department"})
    Optional<LoginActivity> findTopByUserAndStatusOrderByCreatedAtDesc(User user, LoginStatus status);

    @EntityGraph(attributePaths = {"user", "user.department"})
    List<LoginActivity> findByUserOrderByCreatedAtDesc(User user);

    @EntityGraph(attributePaths = {"user", "user.department"})
    List<LoginActivity> findByLoginDateOrderByLoginTimeDesc(LocalDate date);

    @EntityGraph(attributePaths = {"user", "user.department"})
    List<LoginActivity> findByUserAndLoginDateOrderByLoginTimeDesc(User user, LocalDate date);

    @EntityGraph(attributePaths = {"user", "user.department"})
    List<LoginActivity> findByLoginDateBetweenOrderByLoginDateDescLoginTimeDesc(LocalDate start, LocalDate end);

    long countByLoginDateAndStatus(LocalDate date, LoginStatus status);

    long countByStatus(LoginStatus status);

    @EntityGraph(attributePaths = {"user", "user.department"})
    @Query("SELECT la FROM LoginActivity la WHERE la.loginDate = :date ORDER BY la.loginTime DESC")
    List<LoginActivity> findTodayActivity(@Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"user", "user.department"})
    @Query("SELECT la FROM LoginActivity la JOIN la.user u WHERE u.department.id = :deptId AND la.loginDate = :date ORDER BY la.loginTime")
    List<LoginActivity> findByDepartmentAndDate(@Param("deptId") Long deptId, @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"user", "user.department"})
    @Query("SELECT la FROM LoginActivity la WHERE la.loginDate BETWEEN :start AND :end ORDER BY la.loginDate DESC, la.loginTime DESC")
    Page<LoginActivity> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.department"})
    @Query("SELECT la FROM LoginActivity la JOIN la.user u WHERE u.department.id = :deptId ORDER BY la.loginDate DESC, la.loginTime DESC")
    Page<LoginActivity> findByDepartment(@Param("deptId") Long deptId, Pageable pageable);

    long countByUser(User user);

    @EntityGraph(attributePaths = {"user", "user.department"})
    @Query("SELECT la FROM LoginActivity la WHERE la.user.id = :userId ORDER BY la.createdAt DESC")
    Page<LoginActivity> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
