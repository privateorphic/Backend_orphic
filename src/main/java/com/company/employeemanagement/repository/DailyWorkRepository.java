package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.DailyWork;
import com.company.employeemanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyWorkRepository extends JpaRepository<DailyWork, Long> {

    List<DailyWork> findByUserOrderByWorkDateDesc(User user);
    Page<DailyWork> findByUserOrderByWorkDateDesc(User user, Pageable pageable);

    List<DailyWork> findByUserAndWorkDate(User user, LocalDate date);
    List<DailyWork> findByUserAndWorkDateBetween(User user, LocalDate start, LocalDate end);

    @Query("SELECT SUM(dw.hoursWorked) FROM DailyWork dw WHERE dw.user.id = :userId AND dw.workDate = :date")
    BigDecimal sumHoursByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT SUM(dw.hoursWorked) FROM DailyWork dw WHERE dw.user.id = :userId AND dw.workDate BETWEEN :start AND :end")
    BigDecimal sumHoursByUserAndDateRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT dw.workDate, SUM(dw.hoursWorked) FROM DailyWork dw WHERE dw.user.id = :userId AND dw.workDate BETWEEN :start AND :end GROUP BY dw.workDate ORDER BY dw.workDate")
    List<Object[]> dailyHoursForUser(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    Page<DailyWork> findByTaskId(Long taskId, Pageable pageable);
}
