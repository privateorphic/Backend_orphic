package com.company.employeemanagement.dto.attendance;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EmployeeDailyActivityResponse {
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private LocalDate attendanceDate;
    private AttendanceResponse attendance;
    private DailyAttendanceSummaryDto summary;
    private List<TaskSnapshotDto> morningTasks;
    private List<TaskSnapshotDto> eveningTasks;
    private TaskChangeDto taskChanges;
    private List<TaskActivityTimelineDto> timeline;
}
