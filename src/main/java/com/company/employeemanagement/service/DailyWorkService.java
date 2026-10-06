package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.dailywork.DailyWorkRequest;
import com.company.employeemanagement.dto.dailywork.DailyWorkResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DailyWorkService {
    DailyWorkResponse submitDailyWork(DailyWorkRequest request);
    List<DailyWorkResponse> getMyDailyWork();
    Page<DailyWorkResponse> getMyWorkHistory(Pageable pageable);
    Page<DailyWorkResponse> getAllDailyWork(Pageable pageable);
}
