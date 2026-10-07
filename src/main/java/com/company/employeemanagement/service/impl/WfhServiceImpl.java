package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.wfh.*;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.ApiException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.exception.UnauthorizedException;
import com.company.employeemanagement.repository.*;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.AuditLogService;
import com.company.employeemanagement.service.NotificationService;
import com.company.employeemanagement.service.WfhService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WfhServiceImpl implements WfhService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private final LeaveRequestRepository leaveRequestRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final WfhLocationHistoryRepository wfhLocationHistoryRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public WfhResponse createWfhRequest(WfhRequestDto dto) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate reqDate = dto.getDate();

        // 1. Prevent duplicate active WFH requests for same date
        List<LeaveRequest> existingWfh = leaveRequestRepository.findByUserAndLeaveType(currentUser, LeaveType.WORK_FROM_HOME);
        boolean duplicateExists = existingWfh.stream()
                .anyMatch(r -> r.getStartDate().equals(reqDate) && r.getStatus() != LeaveStatus.REJECTED && r.getStatus() != LeaveStatus.CANCELLED);
        if (duplicateExists) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You already have an active WFH request for " + reqDate);
        }

        // 2. Prevent conflict with approved leave
        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findApprovedLeaveOnDate(currentUser, reqDate);
        boolean hasApprovedLeave = approvedLeaves.stream().anyMatch(l -> l.getLeaveType() != LeaveType.WORK_FROM_HOME);
        if (hasApprovedLeave) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot apply for WFH on " + reqDate + " because you have an approved leave on this date.");
        }

        // 3. Prevent conflict with existing office attendance
        var attendanceOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, reqDate);
        if (attendanceOpt.isPresent() && attendanceOpt.get().getWorkMode() == WorkMode.OFFICE && attendanceOpt.get().getStatus() != AttendanceStatus.NOT_STARTED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot apply for WFH on " + reqDate + " because office attendance has already been logged.");
        }

        String fullReason = dto.getReason() + (dto.getNotes() != null && !dto.getNotes().isBlank() ? " | Notes: " + dto.getNotes() : "");

        LeaveRequest wfhRequest = LeaveRequest.builder()
                .user(currentUser)
                .leaveType(LeaveType.WORK_FROM_HOME)
                .startDate(reqDate)
                .endDate(reqDate)
                .totalDays(1)
                .reason(fullReason)
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(wfhRequest);

        auditLogService.log(currentUser, AuditAction.LEAVE_APPLIED, "WfhRequest", saved.getId(),
                "WFH request submitted for " + reqDate);

        // Notify HR & Admin users
        userRepository.findByRole(Role.HR).forEach(hr ->
                notificationService.createNotification(hr, NotificationType.LEAVE_REQUEST,
                        "WFH Request", currentUser.getName() + " requested WFH for " + reqDate, saved.getId(), "LeaveRequest"));
        userRepository.findByRole(Role.ADMIN).forEach(admin ->
                notificationService.createNotification(admin, NotificationType.LEAVE_REQUEST,
                        "WFH Request", currentUser.getName() + " requested WFH for " + reqDate, saved.getId(), "LeaveRequest"));

        return mapToWfhResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WfhResponse> getMyWfhRequests() {
        User currentUser = securityUtils.getCurrentUser();
        return leaveRequestRepository.findByUserAndLeaveType(currentUser, LeaveType.WORK_FROM_HOME).stream()
                .map(this::mapToWfhResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WfhResponse> getAllWfhRequests(LocalDate date, String status, Pageable pageable) {
        List<LeaveRequest> allWfh = leaveRequestRepository.findByLeaveTypeOrderByCreatedAtDesc(LeaveType.WORK_FROM_HOME);
        List<WfhResponse> filtered = allWfh.stream()
                .filter(r -> date == null || r.getStartDate().equals(date))
                .filter(r -> status == null || status.isBlank() || r.getStatus().name().equalsIgnoreCase(status))
                .map(this::mapToWfhResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<WfhResponse> pageContent = start > filtered.size() ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Override
    @Transactional
    public WfhResponse approveWfhRequest(Long id, WfhApprovalRequest approvalRequest) {
        User reviewer = securityUtils.getCurrentUser();
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WFH Request", "id", id));

        if (request.getLeaveType() != LeaveType.WORK_FROM_HOME) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Specified request is not a WFH request");
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only PENDING WFH requests can be approved");
        }

        request.setStatus(LeaveStatus.APPROVED);
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now(IST_ZONE));
        if (approvalRequest != null && approvalRequest.getComments() != null) {
            request.setReviewerComments(approvalRequest.getComments());
        }

        LeaveRequest saved = leaveRequestRepository.save(request);

        auditLogService.log(reviewer, AuditAction.LEAVE_APPROVED, "WfhRequest", saved.getId(),
                "Approved WFH request for " + saved.getUser().getName() + " on date " + saved.getStartDate());

        notificationService.createNotification(
                saved.getUser(),
                NotificationType.LEAVE_APPROVED,
                "WFH Approved",
                "Your WFH request for " + saved.getStartDate() + " has been approved.",
                saved.getId(),
                "LeaveRequest"
        );

        return mapToWfhResponse(saved);
    }

    @Override
    @Transactional
    public WfhResponse rejectWfhRequest(Long id, WfhApprovalRequest approvalRequest) {
        User reviewer = securityUtils.getCurrentUser();
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WFH Request", "id", id));

        if (request.getLeaveType() != LeaveType.WORK_FROM_HOME) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Specified request is not a WFH request");
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only PENDING WFH requests can be rejected");
        }

        String reason = (approvalRequest != null && approvalRequest.getRejectionReason() != null)
                ? approvalRequest.getRejectionReason() : "Request rejected by management.";

        request.setStatus(LeaveStatus.REJECTED);
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now(IST_ZONE));
        request.setReviewerComments(reason);

        LeaveRequest saved = leaveRequestRepository.save(request);

        auditLogService.log(reviewer, AuditAction.LEAVE_REJECTED, "WfhRequest", saved.getId(),
                "Rejected WFH request for " + saved.getUser().getName() + ". Reason: " + reason);

        notificationService.createNotification(
                saved.getUser(),
                NotificationType.LEAVE_REJECTED,
                "WFH Request Rejected",
                "Your WFH request for " + saved.getStartDate() + " was rejected. Reason: " + reason,
                saved.getId(),
                "LeaveRequest"
        );

        return mapToWfhResponse(saved);
    }

    @Override
    @Transactional
    public WfhResponse cancelWfhRequest(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WFH Request", "id", id));

        if (!request.getUser().getId().equals(currentUser.getId())
                && currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.HR) {
            throw new UnauthorizedException("You can only cancel your own WFH requests");
        }

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only PENDING WFH requests can be cancelled");
        }

        request.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest saved = leaveRequestRepository.save(request);

        return mapToWfhResponse(saved);
    }

    @Override
    @Transactional
    public void recordLocation(WfhLocationRequest locationRequest) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now(IST_ZONE);

        var attendanceOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, today);
        if (attendanceOpt.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No active attendance found for today to record location.");
        }

        DailyAttendance attendance = attendanceOpt.get();
        if (attendance.getWorkMode() != WorkMode.WFH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Location tracking is only active for Work From Home (WFH) workdays.");
        }
        if (attendance.getStatus() != AttendanceStatus.WORKING && attendance.getStatus() != AttendanceStatus.CHECKED_IN) {
            log.debug("Skipping location recording as WFH workday status is {}", attendance.getStatus());
            return;
        }

        WfhLocationHistory loc = new WfhLocationHistory();
        loc.setEmployee(currentUser);
        loc.setAttendance(attendance);
        loc.setLatitude(locationRequest.getLatitude());
        loc.setLongitude(locationRequest.getLongitude());
        loc.setAccuracyMeters(locationRequest.getAccuracyMeters());
        loc.setCapturedAt(LocalDateTime.now(IST_ZONE));

        wfhLocationHistoryRepository.save(loc);
    }

    @Override
    @Transactional(readOnly = true)
    public WfhActiveEmployeeDto getLatestLocation(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employeeId));

        var latestOpt = wfhLocationHistoryRepository.findLatestByEmployeeId(employeeId);
        LocalDate today = LocalDate.now(IST_ZONE);
        var attOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(employee, today);

        return buildActiveEmployeeDto(employee, attOpt.orElse(null), latestOpt.orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WfhActiveEmployeeDto> getLocationHistory(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employeeId));

        List<WfhLocationHistory> history = wfhLocationHistoryRepository.findHistoryByEmployeeId(employeeId);
        LocalDate today = LocalDate.now(IST_ZONE);
        var attOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(employee, today);

        return history.stream()
                .map(loc -> buildActiveEmployeeDto(employee, attOpt.orElse(null), loc))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WfhActiveEmployeeDto> getActiveWfhEmployees() {
        LocalDate today = LocalDate.now(IST_ZONE);
        List<DailyAttendance> todayWfhAttendance = dailyAttendanceRepository.findByAttendanceDate(today).stream()
                .filter(a -> a.getWorkMode() == WorkMode.WFH)
                .toList();

        List<WfhActiveEmployeeDto> result = new ArrayList<>();
        for (DailyAttendance att : todayWfhAttendance) {
            User employee = att.getEmployee();
            var latestLoc = wfhLocationHistoryRepository.findLatestByEmployeeId(employee.getId()).orElse(null);
            result.add(buildActiveEmployeeDto(employee, att, latestLoc));
        }

        return result;
    }

    private WfhActiveEmployeeDto buildActiveEmployeeDto(User employee, DailyAttendance attendance, WfhLocationHistory loc) {
        Integer totalTasks = 0;
        Integer completedTasks = 0;
        if (employee != null) {
            var tasks = taskRepository.findByAssignedTo(employee);
            totalTasks = tasks.size();
            completedTasks = (int) tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        }

        String activeSessionTime = "0h 0m";
        if (attendance != null && attendance.getMorningCheckIn() != null) {
            LocalTime end = attendance.getEveningCheckOut() != null ? attendance.getEveningCheckOut() : LocalTime.now(IST_ZONE);
            Duration duration = Duration.between(attendance.getMorningCheckIn(), end);
            activeSessionTime = String.format("%dh %dm", duration.toHours(), duration.toMinutesPart());
        }

        return WfhActiveEmployeeDto.builder()
                .attendanceId(attendance != null ? attendance.getId() : null)
                .employeeId(employee != null ? employee.getId() : null)
                .employeeName(employee != null ? employee.getName() : "Unknown")
                .employeeCode(employee != null ? employee.getEmployeeId() : null)
                .departmentName(employee != null && employee.getDepartment() != null ? employee.getDepartment().getName() : "General")
                .workMode(attendance != null ? attendance.getWorkMode() : WorkMode.WFH)
                .status(attendance != null ? attendance.getStatus() : AttendanceStatus.NOT_STARTED)
                .checkInTime(attendance != null ? attendance.getMorningCheckIn() : null)
                .checkOutTime(attendance != null ? attendance.getEveningCheckOut() : null)
                .activeSessionTime(activeSessionTime)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .lastLatitude(loc != null ? loc.getLatitude() : (attendance != null ? attendance.getMorningLatitude() : null))
                .lastLongitude(loc != null ? loc.getLongitude() : (attendance != null ? attendance.getMorningLongitude() : null))
                .lastAccuracyMeters(loc != null ? loc.getAccuracyMeters() : null)
                .lastCapturedAt(loc != null ? loc.getCapturedAt() : null)
                .build();
    }

    private WfhResponse mapToWfhResponse(LeaveRequest req) {
        String reasonStr = req.getReason();
        String notesStr = null;
        if (reasonStr != null && reasonStr.contains(" | Notes: ")) {
            String[] parts = reasonStr.split(" \\| Notes: ");
            reasonStr = parts[0];
            notesStr = parts.length > 1 ? parts[1] : null;
        }

        return WfhResponse.builder()
                .id(req.getId())
                .employeeId(req.getUser().getId())
                .employeeName(req.getUser().getName())
                .employeeCode(req.getUser().getEmployeeId())
                .departmentName(req.getUser().getDepartment() != null ? req.getUser().getDepartment().getName() : "General")
                .date(req.getStartDate())
                .reason(reasonStr)
                .notes(notesStr)
                .status(req.getStatus())
                .approvedByName(req.getReviewedBy() != null ? req.getReviewedBy().getName() : null)
                .approvedAt(req.getReviewedAt())
                .rejectionReason(req.getStatus() == LeaveStatus.REJECTED ? req.getReviewerComments() : null)
                .createdAt(req.getCreatedAt())
                .build();
    }
}
