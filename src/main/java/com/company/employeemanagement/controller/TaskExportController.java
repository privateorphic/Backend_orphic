package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.report.TaskExportFilterRequest;
import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.TaskExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping({"/api/admin/tasks", "/api/v1/admin/tasks"})
@RequiredArgsConstructor
public class TaskExportController {

    private final TaskExportService taskExportService;
    private final SecurityUtils securityUtils;

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<InputStreamResource> exportTasks(
            @ModelAttribute TaskExportFilterRequest filterRequest
    ) {
        User currentUser = securityUtils.getCurrentUser();
        ByteArrayInputStream in = taskExportService.exportTasksToExcel(filterRequest, currentUser);

        LocalDate from = filterRequest.getFromDate() != null ? filterRequest.getFromDate() : LocalDate.now(ZoneId.of("Asia/Kolkata")).withDayOfMonth(1);
        LocalDate to = filterRequest.getToDate() != null ? filterRequest.getToDate() : LocalDate.now(ZoneId.of("Asia/Kolkata"));

        String filename = String.format("Employee_Work_Report_%s_to_%s.xlsx",
                from.format(DateTimeFormatter.ISO_DATE),
                to.format(DateTimeFormatter.ISO_DATE));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
