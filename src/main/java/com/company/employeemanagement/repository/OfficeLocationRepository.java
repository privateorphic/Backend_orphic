package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.OfficeLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeLocationRepository extends JpaRepository<OfficeLocation, Long> {
    Optional<OfficeLocation> findFirstByActiveTrue();
}
