package com.company.employeemanagement.dto.attendance;

import com.company.employeemanagement.entity.AttendanceStatus;
import com.company.employeemanagement.entity.WorkMode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private LocalDate attendanceDate;
    private WorkMode workMode;
    private Boolean isWfhApprovedToday;
    private LocalTime morningCheckIn;
    private Double morningLatitude;
    private Double morningLongitude;
    private Double morningDistanceFromOffice;
    private LocalTime eveningCheckOut;
    private Double eveningLatitude;
    private Double eveningLongitude;
    private Double eveningDistanceFromOffice;
    private AttendanceStatus status;
    private String officeDuration;
}
