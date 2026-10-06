package com.company.employeemanagement.dto.wfh;

import com.company.employeemanagement.entity.AttendanceStatus;
import com.company.employeemanagement.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WfhActiveEmployeeDto {
    private Long attendanceId;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private WorkMode workMode;
    private AttendanceStatus status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String activeSessionTime;
    private Integer totalTasks;
    private Integer completedTasks;
    private Double lastLatitude;
    private Double lastLongitude;
    private Double lastAccuracyMeters;
    private LocalDateTime lastCapturedAt;
}
