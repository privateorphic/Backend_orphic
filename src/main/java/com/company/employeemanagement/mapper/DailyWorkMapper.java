package com.company.employeemanagement.mapper;

import com.company.employeemanagement.dto.dailywork.DailyWorkResponse;
import com.company.employeemanagement.entity.DailyWork;
import org.springframework.stereotype.Component;

@Component
public class DailyWorkMapper {

    public DailyWorkResponse toDailyWorkResponse(DailyWork dw) {
        if (dw == null) return null;
        return DailyWorkResponse.builder()
                .id(dw.getId())
                .userId(dw.getUser() != null ? dw.getUser().getId() : null)
                .employeeName(dw.getUser() != null ? dw.getUser().getName() : null)
                .employeeId(dw.getUser() != null ? dw.getUser().getEmployeeId() : null)
                .taskId(dw.getTask() != null ? dw.getTask().getId() : null)
                .taskTitle(dw.getTask() != null ? dw.getTask().getTitle() : null)
                .workDate(dw.getWorkDate())
                .description(dw.getDescription())
                .hoursWorked(dw.getHoursWorked())
                .progressPercentage(dw.getProgressPercentage())
                .status(dw.getStatus())
                .notes(dw.getNotes())
                .createdAt(dw.getCreatedAt())
                .build();
    }
}
