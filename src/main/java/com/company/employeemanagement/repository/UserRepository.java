package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.Role;
import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByEmailOrEmployeeId(String email, String employeeId);

    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);

    long countByStatus(UserStatus status);
    long countByRole(Role role);
    long countByRoleAndStatus(Role role, UserStatus status);
    long countByJoiningDateBetween(LocalDate start, LocalDate end);

    List<User> findByDepartmentId(Long departmentId);
    List<User> findByRole(Role role);
    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    List<User> findActiveByRole(@Param("role") Role role);

    @Query("SELECT u.department.name, COUNT(u) FROM User u WHERE u.status = 'ACTIVE' GROUP BY u.department.name")
    List<Object[]> countByDepartment();

    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByDepartmentId(Long departmentId, Pageable pageable);
}
