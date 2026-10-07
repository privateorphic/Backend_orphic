package com.company.employeemanagement.dto.attendance;

import com.company.employeemanagement.entity.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class DailyAttendanceSummaryDto {
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private LocalTime morningCheckIn;
    private Double morningLatitude;
    private Double morningLongitude;
    private Double morningDistanceFromOffice;
    private LocalTime eveningCheckOut;
    private Double eveningLatitude;
    private Double eveningLongitude;
    private Double eveningDistanceFromOffice;
    private String officeDuration;
    private int morningTaskCount;
    private int tasksAdded;
    private int finalTaskCount;
    private int completedTaskCount;
    private int inProgressTaskCount;
    private int pendingTaskCount;
}
