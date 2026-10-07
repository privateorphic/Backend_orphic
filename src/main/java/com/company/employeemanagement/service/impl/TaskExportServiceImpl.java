package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.report.TaskExportFilterRequest;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.repository.*;
import com.company.employeemanagement.service.TaskExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TaskExportServiceImpl implements TaskExportService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DailyWorkRepository dailyWorkRepository;

    @Autowired
    private DailyAttendanceRepository dailyAttendanceRepository;

    @Autowired
    private LoginActivityRepository loginActivityRepository;

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportTasksToExcel(TaskExportFilterRequest filter, User currentUser) {
        LocalDate fromDate = filter.getFromDate() != null ? filter.getFromDate() : LocalDate.now(IST_ZONE).withDayOfMonth(1);
        LocalDate toDate = filter.getToDate() != null ? filter.getToDate() : LocalDate.now(IST_ZONE);

        // Fetch data
        List<Task> allTasks = taskRepository.findAll();
        List<User> allEmployees = userRepository.findAll();
        List<DailyWork> allDailyWork = dailyWorkRepository.findAll();
        List<DailyAttendance> allAttendance = dailyAttendanceRepository.findAll();
        List<LoginActivity> allSessions = loginActivityRepository.findAll();

        // Apply filters
        List<Task> filteredTasks = allTasks.stream().filter(t -> {
            LocalDate taskDate = t.getStartDate() != null ? t.getStartDate() : (t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate() : LocalDate.now());
            if (taskDate.isBefore(fromDate) || taskDate.isAfter(toDate)) return false;
            if (filter.getEmployeeId() != null && (t.getAssignedTo() == null || !t.getAssignedTo().getId().equals(filter.getEmployeeId()))) return false;
            if (filter.getDepartmentId() != null && (t.getDepartment() == null || !t.getDepartment().getId().equals(filter.getDepartmentId()))) return false;
            if (filter.getStatus() != null && !t.getStatus().equals(filter.getStatus())) return false;
            if (filter.getPriority() != null && !t.getPriority().equals(filter.getPriority())) return false;
            return true;
        }).toList();

        List<DailyWork> filteredDailyWork = allDailyWork.stream().filter(dw -> {
            if (dw.getWorkDate().isBefore(fromDate) || dw.getWorkDate().isAfter(toDate)) return false;
            if (filter.getEmployeeId() != null && !dw.getUser().getId().equals(filter.getEmployeeId())) return false;
            if (filter.getDepartmentId() != null && (dw.getUser().getDepartment() == null || !dw.getUser().getDepartment().getId().equals(filter.getDepartmentId()))) return false;
            return true;
        }).toList();

        List<DailyAttendance> filteredAttendance = allAttendance.stream().filter(da -> {
            if (da.getAttendanceDate().isBefore(fromDate) || da.getAttendanceDate().isAfter(toDate)) return false;
            if (filter.getEmployeeId() != null && !da.getEmployee().getId().equals(filter.getEmployeeId())) return false;
            if (filter.getDepartmentId() != null && (da.getEmployee().getDepartment() == null || !da.getEmployee().getDepartment().getId().equals(filter.getDepartmentId()))) return false;
            return true;
        }).toList();

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Styles
            DataFormat dataFormat = workbook.createDataFormat();
            
            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // SubHeader / Title Style
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFont(titleFont);

            // Metric Card Style
            CellStyle kpiTitleStyle = workbook.createCellStyle();
            Font kpiTitleFont = workbook.createFont();
            kpiTitleFont.setBold(true);
            kpiTitleFont.setFontHeightInPoints((short) 9);
            kpiTitleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            kpiTitleStyle.setFont(kpiTitleFont);

            CellStyle kpiValStyle = workbook.createCellStyle();
            Font kpiValFont = workbook.createFont();
            kpiValFont.setBold(true);
            kpiValFont.setFontHeightInPoints((short) 14);
            kpiValFont.setColor(IndexedColors.BLACK.getIndex());
            kpiValStyle.setFont(kpiValFont);

            // General Cell Styles
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(dataFormat.getFormat("0.00%"));

            CellStyle decimalStyle = workbook.createCellStyle();
            decimalStyle.setDataFormat(dataFormat.getFormat("0.00"));

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(dataFormat.getFormat("dd-mmm-yyyy"));

            // -------------------------------------------------------------
            // SHEET 1: Employee Summary
            // -------------------------------------------------------------
            XSSFSheet sheet1 = workbook.createSheet("Employee Summary");

            // Report Banner
            Row r0 = sheet1.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue("EMPLOYEE WORK REPORT");
            c0.setCellStyle(titleStyle);

            Row r1 = sheet1.createRow(1);
            r1.createCell(0).setCellValue("Report Period: " + fromDate.format(DATE_FORMATTER) + " to " + toDate.format(DATE_FORMATTER));
            Row r2 = sheet1.createRow(2);
            r2.createCell(0).setCellValue("Generated On: " + LocalDateTime.now(IST_ZONE).format(DATETIME_FORMATTER) + " | Generated By: " + (currentUser != null ? currentUser.getName() : "Admin"));

            // Overall Summary KPI Blocks
            long totalTasksCount = filteredTasks.size();
            long completedTasksCount = filteredTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
            long inProgressTasksCount = filteredTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
            long pendingTasksCount = filteredTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
            double totalHoursWorkedSum = filteredDailyWork.stream().mapToDouble(dw -> dw.getHoursWorked() != null ? dw.getHoursWorked().doubleValue() : 0.0).sum();
            double overallCompletionPct = totalTasksCount > 0 ? (double) completedTasksCount / totalTasksCount : 0.0;

            Row r4 = sheet1.createRow(4);
            r4.createCell(0).setCellValue("Total Employees");
            r4.createCell(2).setCellValue("Total Tasks");
            r4.createCell(4).setCellValue("Completed Tasks");
            r4.createCell(6).setCellValue("In Progress");
            r4.createCell(8).setCellValue("Pending Tasks");
            r4.createCell(10).setCellValue("Total Hours Worked");
            r4.createCell(12).setCellValue("Overall Completion %");

            for (int i = 0; i <= 12; i += 2) {
                if (r4.getCell(i) != null) r4.getCell(i).setCellStyle(kpiTitleStyle);
            }

            Row r5 = sheet1.createRow(5);
            Set<Long> empIdsWithData = new HashSet<>();
            filteredTasks.forEach(t -> { if (t.getAssignedTo() != null) empIdsWithData.add(t.getAssignedTo().getId()); });
            filteredDailyWork.forEach(dw -> empIdsWithData.add(dw.getUser().getId()));

            r5.createCell(0).setCellValue(empIdsWithData.size());
            r5.createCell(2).setCellValue(totalTasksCount);
            r5.createCell(4).setCellValue(completedTasksCount);
            r5.createCell(6).setCellValue(inProgressTasksCount);
            r5.createCell(8).setCellValue(pendingTasksCount);
            
            Cell cellHours = r5.createCell(10);
            cellHours.setCellValue(totalHoursWorkedSum);
            cellHours.setCellStyle(decimalStyle);

            Cell cellPct = r5.createCell(12);
            cellPct.setCellValue(overallCompletionPct);
            cellPct.setCellStyle(percentStyle);

            for (int i = 0; i <= 8; i += 2) {
                if (r5.getCell(i) != null) r5.getCell(i).setCellStyle(kpiValStyle);
            }

            // Table Headers for Sheet 1
            int s1RowIdx = 7;
            Row s1HeaderRow = sheet1.createRow(s1RowIdx++);
            String[] s1Headers = {
                "Employee ID", "Employee Name", "Email", "Department", "Total Tasks",
                "Completed Tasks", "In Progress Tasks", "Pending Tasks", "Overdue Tasks",
                "Completion %", "Total Hours Worked", "Average Hours / Task", "Tasks Per Working Day", "Active Working Days"
            };
            for (int col = 0; col < s1Headers.length; col++) {
                Cell cell = s1HeaderRow.createCell(col);
                cell.setCellValue(s1Headers[col]);
                cell.setCellStyle(headerStyle);
            }

            // Populate Sheet 1 Data
            List<User> targetEmployees = allEmployees.stream().filter(e -> filter.getEmployeeId() == null || e.getId().equals(filter.getEmployeeId())).toList();

            for (User emp : targetEmployees) {
                List<Task> empTasks = filteredTasks.stream().filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(emp.getId())).toList();
                List<DailyWork> empWork = filteredDailyWork.stream().filter(dw -> dw.getUser().getId().equals(emp.getId())).toList();
                List<DailyAttendance> empAtt = filteredAttendance.stream().filter(da -> da.getEmployee().getId().equals(emp.getId())).toList();

                if (empTasks.isEmpty() && empWork.isEmpty() && empAtt.isEmpty() && filter.getEmployeeId() == null) {
                    continue; // Skip employees with no activity if showing all
                }

                long eTotal = empTasks.size();
                long eCompleted = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
                long eInProgress = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
                long ePending = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
                long eOverdue = empTasks.stream().filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now(IST_ZONE)) && t.getStatus() != TaskStatus.COMPLETED).count();

                double eCompletionPct = eTotal > 0 ? (double) eCompleted / eTotal : 0.0;
                double eHoursWorked = empWork.stream().mapToDouble(dw -> dw.getHoursWorked() != null ? dw.getHoursWorked().doubleValue() : 0.0).sum();
                double eAvgHoursPerTask = eTotal > 0 ? eHoursWorked / eTotal : 0.0;

                Set<LocalDate> activeDays = new HashSet<>();
                empAtt.forEach(da -> activeDays.add(da.getAttendanceDate()));
                empWork.forEach(dw -> activeDays.add(dw.getWorkDate()));
                int activeWorkingDaysCount = Math.max(1, activeDays.size());
                double eTasksPerDay = (double) eTotal / activeWorkingDaysCount;

                Row row = sheet1.createRow(s1RowIdx++);
                row.createCell(0).setCellValue(emp.getEmployeeId() != null ? emp.getEmployeeId() : "EMP" + emp.getId());
                row.createCell(1).setCellValue(emp.getName());
                row.createCell(2).setCellValue(emp.getEmail() != null ? emp.getEmail() : "");
                row.createCell(3).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A");
                row.createCell(4).setCellValue(eTotal);
                row.createCell(5).setCellValue(eCompleted);
                row.createCell(6).setCellValue(eInProgress);
                row.createCell(7).setCellValue(ePending);
                row.createCell(8).setCellValue(eOverdue);

                Cell cPct = row.createCell(9);
                cPct.setCellValue(eCompletionPct);
                cPct.setCellStyle(percentStyle);

                Cell cHrs = row.createCell(10);
                cHrs.setCellValue(eHoursWorked);
                cHrs.setCellStyle(decimalStyle);

                Cell cAvgHrs = row.createCell(11);
                cAvgHrs.setCellValue(eAvgHoursPerTask);
                cAvgHrs.setCellStyle(decimalStyle);

                Cell cTpd = row.createCell(12);
                cTpd.setCellValue(eTasksPerDay);
                cTpd.setCellStyle(decimalStyle);

                row.createCell(13).setCellValue(activeDays.size());
            }

            sheet1.createFreezePane(0, 8);
            if (s1RowIdx > 8) {
                sheet1.setAutoFilter(new CellRangeAddress(7, s1RowIdx - 1, 0, s1Headers.length - 1));
            }

            // -------------------------------------------------------------
            // SHEET 2: Daily Summary
            // -------------------------------------------------------------
            XSSFSheet sheet2 = workbook.createSheet("Daily Summary");
            Row s2HeaderRow = sheet2.createRow(0);
            String[] s2Headers = {
                "Date", "Employee ID", "Employee Name", "Department", "Work Mode",
                "Check-In", "Check-Out", "Active Working Time", "Total Tasks", "Completed Tasks",
                "In Progress Tasks", "Pending Tasks", "Tasks Added", "Tasks Completed", "Total Hours Worked", "Completion %"
            };
            for (int col = 0; col < s2Headers.length; col++) {
                Cell cell = s2HeaderRow.createCell(col);
                cell.setCellValue(s2Headers[col]);
                cell.setCellStyle(headerStyle);
            }

            int s2RowIdx = 1;
            for (DailyAttendance da : filteredAttendance) {
                User emp = da.getEmployee();
                LocalDate date = da.getAttendanceDate();

                List<Task> dayTasks = filteredTasks.stream().filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(emp.getId()) &&
                    (t.getStartDate() != null ? t.getStartDate().equals(date) : (t.getCreatedAt() != null && t.getCreatedAt().toLocalDate().equals(date)))).toList();
                List<DailyWork> dayWork = filteredDailyWork.stream().filter(dw -> dw.getUser().getId().equals(emp.getId()) && dw.getWorkDate().equals(date)).toList();

                long dTotal = dayTasks.size();
                long dCompleted = dayTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
                long dInProgress = dayTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
                long dPending = dayTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
                double dHours = dayWork.stream().mapToDouble(dw -> dw.getHoursWorked() != null ? dw.getHoursWorked().doubleValue() : 0.0).sum();
                double dPct = dTotal > 0 ? (double) dCompleted / dTotal : 0.0;

                Row row = sheet2.createRow(s2RowIdx++);
                row.createCell(0).setCellValue(date.format(DATE_FORMATTER));
                row.createCell(1).setCellValue(emp.getEmployeeId() != null ? emp.getEmployeeId() : "EMP" + emp.getId());
                row.createCell(2).setCellValue(emp.getName());
                row.createCell(3).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A");
                row.createCell(4).setCellValue(da.getWorkMode() != null ? da.getWorkMode().name() : "N/A");
                row.createCell(5).setCellValue(da.getMorningCheckIn() != null ? da.getMorningCheckIn().format(TIME_FORMATTER) : "N/A");
                row.createCell(6).setCellValue(da.getEveningCheckOut() != null ? da.getEveningCheckOut().format(TIME_FORMATTER) : "N/A");
                row.createCell(7).setCellValue(da.getOfficeDuration() != null ? da.getOfficeDuration() : "N/A");
                row.createCell(8).setCellValue(dTotal);
                row.createCell(9).setCellValue(dCompleted);
                row.createCell(10).setCellValue(dInProgress);
                row.createCell(11).setCellValue(dPending);
                row.createCell(12).setCellValue(dayTasks.size());
                row.createCell(13).setCellValue(dCompleted);

                Cell cH = row.createCell(14);
                cH.setCellValue(dHours);
                cH.setCellStyle(decimalStyle);

                Cell cP = row.createCell(15);
                cP.setCellValue(dPct);
                cP.setCellStyle(percentStyle);
            }

            sheet2.createFreezePane(0, 1);
            if (s2RowIdx > 1) {
                sheet2.setAutoFilter(new CellRangeAddress(0, s2RowIdx - 1, 0, s2Headers.length - 1));
            }

            // -------------------------------------------------------------
            // SHEET 3: Task Details
            // -------------------------------------------------------------
            XSSFSheet sheet3 = workbook.createSheet("Task Details");
            Row s3HeaderRow = sheet3.createRow(0);
            String[] s3Headers = {
                "Task ID", "Work Date", "Employee ID", "Employee Name", "Department",
                "Task Title", "Task Description", "Priority", "Status", "Progress %",
                "Created At", "Completed At", "Deadline", "Is Overdue", "Work Update"
            };
            for (int col = 0; col < s3Headers.length; col++) {
                Cell cell = s3HeaderRow.createCell(col);
                cell.setCellValue(s3Headers[col]);
                cell.setCellStyle(headerStyle);
            }

            int s3RowIdx = 1;
            for (Task t : filteredTasks) {
                User emp = t.getAssignedTo();
                LocalDate workDate = t.getStartDate() != null ? t.getStartDate() : (t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate() : LocalDate.now());
                boolean isOverdue = t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now(IST_ZONE)) && t.getStatus() != TaskStatus.COMPLETED;

                Row row = sheet3.createRow(s3RowIdx++);
                row.createCell(0).setCellValue("TSK-" + t.getId());
                row.createCell(1).setCellValue(workDate.format(DATE_FORMATTER));
                row.createCell(2).setCellValue(emp != null ? (emp.getEmployeeId() != null ? emp.getEmployeeId() : "EMP" + emp.getId()) : "Unassigned");
                row.createCell(3).setCellValue(emp != null ? emp.getName() : "Unassigned");
                row.createCell(4).setCellValue(t.getDepartment() != null ? t.getDepartment().getName() : (emp != null && emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A"));
                row.createCell(5).setCellValue(t.getTitle() != null ? t.getTitle() : "");
                row.createCell(6).setCellValue(t.getDescription() != null ? t.getDescription() : "");
                row.createCell(7).setCellValue(t.getPriority() != null ? t.getPriority().name() : "MEDIUM");
                row.createCell(8).setCellValue(t.getStatus() != null ? t.getStatus().name() : "TODO");

                Cell cProg = row.createCell(9);
                cProg.setCellValue(t.getProgressPercentage() != null ? (double) t.getProgressPercentage() / 100.0 : 0.0);
                cProg.setCellStyle(percentStyle);

                row.createCell(10).setCellValue(t.getCreatedAt() != null ? t.getCreatedAt().format(DATETIME_FORMATTER) : "N/A");
                row.createCell(11).setCellValue(t.getCompletedAt() != null ? t.getCompletedAt().format(DATETIME_FORMATTER) : "N/A");
                row.createCell(12).setCellValue(t.getDeadline() != null ? t.getDeadline().format(DATE_FORMATTER) : "N/A");
                row.createCell(13).setCellValue(isOverdue ? "YES" : "NO");
                row.createCell(14).setCellValue(t.getWorkUpdate() != null ? t.getWorkUpdate() : "");
            }

            sheet3.createFreezePane(0, 1);
            if (s3RowIdx > 1) {
                sheet3.setAutoFilter(new CellRangeAddress(0, s3RowIdx - 1, 0, s3Headers.length - 1));
            }

            // -------------------------------------------------------------
            // SHEET 4: Employee Performance
            // -------------------------------------------------------------
            XSSFSheet sheet4 = workbook.createSheet("Employee Performance");
            Row s4HeaderRow = sheet4.createRow(0);
            String[] s4Headers = {
                "Employee Name", "Department", "Working Days", "Total Tasks",
                "Completed Tasks", "Completion %", "Total Hours", "Tasks / Day", "Avg Hours / Task", "Overdue Tasks"
            };
            for (int col = 0; col < s4Headers.length; col++) {
                Cell cell = s4HeaderRow.createCell(col);
                cell.setCellValue(s4Headers[col]);
                cell.setCellStyle(headerStyle);
            }

            int s4RowIdx = 1;
            for (User emp : targetEmployees) {
                List<Task> empTasks = filteredTasks.stream().filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(emp.getId())).toList();
                List<DailyWork> empWork = filteredDailyWork.stream().filter(dw -> dw.getUser().getId().equals(emp.getId())).toList();
                List<DailyAttendance> empAtt = filteredAttendance.stream().filter(da -> da.getEmployee().getId().equals(emp.getId())).toList();

                if (empTasks.isEmpty() && empWork.isEmpty() && empAtt.isEmpty() && filter.getEmployeeId() == null) continue;

                long total = empTasks.size();
                long completed = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
                long overdue = empTasks.stream().filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now(IST_ZONE)) && t.getStatus() != TaskStatus.COMPLETED).count();

                double pct = total > 0 ? (double) completed / total : 0.0;
                double hrs = empWork.stream().mapToDouble(dw -> dw.getHoursWorked() != null ? dw.getHoursWorked().doubleValue() : 0.0).sum();

                Set<LocalDate> activeDays = new HashSet<>();
                empAtt.forEach(da -> activeDays.add(da.getAttendanceDate()));
                empWork.forEach(dw -> activeDays.add(dw.getWorkDate()));
                int days = Math.max(1, activeDays.size());
                double tasksPerDay = (double) total / days;
                double avgHrsPerTask = total > 0 ? hrs / total : 0.0;

                Row row = sheet4.createRow(s4RowIdx++);
                row.createCell(0).setCellValue(emp.getName());
                row.createCell(1).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A");
                row.createCell(2).setCellValue(activeDays.size());
                row.createCell(3).setCellValue(total);
                row.createCell(4).setCellValue(completed);

                Cell cP = row.createCell(5);
                cP.setCellValue(pct);
                cP.setCellStyle(percentStyle);

                Cell cH = row.createCell(6);
                cH.setCellValue(hrs);
                cH.setCellStyle(decimalStyle);

                Cell cTpd = row.createCell(7);
                cTpd.setCellValue(tasksPerDay);
                cTpd.setCellStyle(decimalStyle);

                Cell cAvg = row.createCell(8);
                cAvg.setCellValue(avgHrsPerTask);
                cAvg.setCellStyle(decimalStyle);

                row.createCell(9).setCellValue(overdue);
            }

            sheet4.createFreezePane(0, 1);
            if (s4RowIdx > 1) {
                sheet4.setAutoFilter(new CellRangeAddress(0, s4RowIdx - 1, 0, s4Headers.length - 1));
            }

            // -------------------------------------------------------------
            // SHEET 5: Status Analysis
            // -------------------------------------------------------------
            XSSFSheet sheet5 = workbook.createSheet("Status Analysis");
            Row s5HeaderRow = sheet5.createRow(0);
            String[] s5Headers = {
                "Employee", "Completed", "In Progress", "Pending", "Overdue", "Total", "Completion %"
            };
            for (int col = 0; col < s5Headers.length; col++) {
                Cell cell = s5HeaderRow.createCell(col);
                cell.setCellValue(s5Headers[col]);
                cell.setCellStyle(headerStyle);
            }

            int s5RowIdx = 1;
            for (User emp : targetEmployees) {
                List<Task> empTasks = filteredTasks.stream().filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(emp.getId())).toList();
                if (empTasks.isEmpty() && filter.getEmployeeId() == null) continue;

                long comp = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
                long inp = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
                long pnd = empTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
                long ovd = empTasks.stream().filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(LocalDate.now(IST_ZONE)) && t.getStatus() != TaskStatus.COMPLETED).count();
                long tot = empTasks.size();
                double pct = tot > 0 ? (double) comp / tot : 0.0;

                Row row = sheet5.createRow(s5RowIdx++);
                row.createCell(0).setCellValue(emp.getName());
                row.createCell(1).setCellValue(comp);
                row.createCell(2).setCellValue(inp);
                row.createCell(3).setCellValue(pnd);
                row.createCell(4).setCellValue(ovd);
                row.createCell(5).setCellValue(tot);

                Cell cP = row.createCell(6);
                cP.setCellValue(pct);
                cP.setCellStyle(percentStyle);
            }

            sheet5.createFreezePane(0, 1);
            if (s5RowIdx > 1) {
                sheet5.setAutoFilter(new CellRangeAddress(0, s5RowIdx - 1, 0, s5Headers.length - 1));
            }

            // Auto-size columns for all sheets
            XSSFSheet[] sheets = {sheet1, sheet2, sheet3, sheet4, sheet5};
            for (XSSFSheet s : sheets) {
                int colCount = s.getRow(s == sheet1 ? 7 : 0) != null ? s.getRow(s == sheet1 ? 7 : 0).getLastCellNum() : 10;
                for (int c = 0; c < colCount; c++) {
                    s.autoSizeColumn(c);
                    int curWidth = s.getColumnWidth(c);
                    s.setColumnWidth(c, Math.min(Math.max(curWidth + 1024, 3000), 12000));
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel task report", e);
        }
    }
}
