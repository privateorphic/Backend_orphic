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
    private LocalTime eveningCheckOut;
    private String officeDuration;
    private int morningTaskCount;
    private int tasksAdded;
    private int finalTaskCount;
    private int completedTaskCount;
    private int inProgressTaskCount;
    private int pendingTaskCount;
}
