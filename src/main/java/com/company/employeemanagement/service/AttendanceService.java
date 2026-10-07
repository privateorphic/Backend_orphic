package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.attendance.*;
import com.company.employeemanagement.entity.*;
import com.company.employeemanagement.exception.ApiException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.repository.*;
import com.company.employeemanagement.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final DailyTaskSnapshotRepository dailyTaskSnapshotRepository;
    private final TaskActivityHistoryRepository taskActivityHistoryRepository;
    private final TaskRepository taskRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final LocationValidationService locationValidationService;
    private final TaskSnapshotService taskSnapshotService;
    private final AuditLogService auditLogService;
    private final WfhLocationHistoryRepository wfhLocationHistoryRepository;
    private final NotificationService notificationService;
    private final LoginActivityRepository loginActivityRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Transactional
    public AttendanceResponse checkIn(CheckInRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now(IST_ZONE);

        Optional<DailyAttendance> existingOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, today);

        if (existingOpt.isPresent()) {
            DailyAttendance existing = existingOpt.get();
            if (existing.getStatus() == AttendanceStatus.CHECKED_IN || existing.getStatus() == AttendanceStatus.CHECKED_OUT) {
                String formattedTime = existing.getMorningCheckIn() != null ? existing.getMorningCheckIn().format(TIME_FORMATTER) : "earlier";
                throw new ApiException(HttpStatus.BAD_REQUEST, "You have already checked in today at " + formattedTime + ".");
            }
        }

        // Check if employee has an APPROVED WFH request for today
        List<LeaveRequest> approvedWfhList = leaveRequestRepository.findApprovedWfhRequestOnDate(currentUser, today);
        boolean isWfhApproved = !approvedWfhList.isEmpty();

        // Location Verification
        OfficeLocation office = locationValidationService.getOrCreateDefaultOfficeLocation();
        double distance = (request.getLatitude() != null && request.getLongitude() != null)
                ? locationValidationService.calculateDistanceMeters(
                        request.getLatitude(), request.getLongitude(),
                        office.getLatitude(), office.getLongitude()
                  )
                : 0.0;

        if (!isWfhApproved && !locationValidationService.isWithinRadius(distance, office.getRadiusMeters())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, String.format(
                    "You are outside the authorized office area. Distance: %.0f meters. Allowed: %.0f meters. Submit a Work From Home (WFH) request on the Leaves page and get HR/Admin approval to check in from home.",
                    distance, office.getRadiusMeters()
            ));
        }

        DailyAttendance attendance = existingOpt.orElseGet(DailyAttendance::new);
        attendance.setEmployee(currentUser);
        attendance.setAttendanceDate(today);
        attendance.setMorningCheckIn(LocalTime.now(IST_ZONE).truncatedTo(ChronoUnit.SECONDS));
        attendance.setMorningLatitude(request.getLatitude());
        attendance.setMorningLongitude(request.getLongitude());
        attendance.setMorningDistanceFromOffice(distance);
        attendance.setOfficeLocation(office);
        attendance.setStatus(AttendanceStatus.CHECKED_IN);

        DailyAttendance saved = dailyAttendanceRepository.save(attendance);

        // Capture immutable Morning Task Snapshot
        taskSnapshotService.captureTaskSnapshot(currentUser, saved, SnapshotType.MORNING);

        auditLogService.log(
                currentUser,
                AuditAction.LOGIN,
                "DailyAttendance",
                saved.getId(),
                "Morning check-in completed at " + saved.getMorningCheckIn().format(TIME_FORMATTER)
        );

        return mapToResponse(saved);
    }

    @Transactional
    public AttendanceResponse checkOut(CheckOutRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now(IST_ZONE);

        DailyAttendance attendance = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, today)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No morning check-in found for today. Please check in first."));

        if (attendance.getStatus() == AttendanceStatus.CHECKED_OUT) {
            String formattedTime = attendance.getEveningCheckOut() != null ? attendance.getEveningCheckOut().format(TIME_FORMATTER) : "earlier";
            throw new ApiException(HttpStatus.BAD_REQUEST, "You have already checked out for today at " + formattedTime + ".");
        }

        LocalTime checkoutTime = LocalTime.now(IST_ZONE).truncatedTo(ChronoUnit.SECONDS);
        attendance.setEveningCheckOut(checkoutTime);

        if (request != null && request.getLatitude() != null && request.getLongitude() != null) {
            attendance.setEveningLatitude(request.getLatitude());
            attendance.setEveningLongitude(request.getLongitude());
            OfficeLocation office = attendance.getOfficeLocation() != null
                    ? attendance.getOfficeLocation()
                    : locationValidationService.getOrCreateDefaultOfficeLocation();
            double distance = locationValidationService.calculateDistanceMeters(
                    request.getLatitude(), request.getLongitude(),
                    office.getLatitude(), office.getLongitude()
            );
            attendance.setEveningDistanceFromOffice(distance);
        }

        // Calculate Office Duration
        if (attendance.getMorningCheckIn() != null) {
            Duration duration = Duration.between(attendance.getMorningCheckIn(), checkoutTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            attendance.setOfficeDuration(String.format("%dh %dm", hours, minutes));
        }

        attendance.setStatus(AttendanceStatus.CHECKED_OUT);
        DailyAttendance saved = dailyAttendanceRepository.save(attendance);

        // Capture Evening Task Snapshot
        taskSnapshotService.captureTaskSnapshot(currentUser, saved, SnapshotType.EVENING);

        auditLogService.log(
                currentUser,
                AuditAction.LOGOUT,
                "DailyAttendance",
                saved.getId(),
                "Evening check-out completed at " + saved.getEveningCheckOut().format(TIME_FORMATTER)
        );

        return mapToResponse(saved);
    }

    @Transactional
    public AttendanceResponse wfhCheckIn(CheckInRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now(IST_ZONE);

        List<LeaveRequest> approvedWfh = leaveRequestRepository.findApprovedWfhRequestOnDate(currentUser, today);
        if (approvedWfh.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No approved Work From Home (WFH) request found for today. Please submit a WFH request and wait for HR/Admin approval.");
        }

        Optional<DailyAttendance> existingOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, today);
        if (existingOpt.isPresent()) {
            DailyAttendance existing = existingOpt.get();
            // If employee re-logins during an active WFH shift, continue existing attendance without duplicate creation!
            if (existing.getStatus() == AttendanceStatus.WORKING || existing.getStatus() == AttendanceStatus.CHECKED_IN || existing.getStatus() == AttendanceStatus.COMPLETED || existing.getStatus() == AttendanceStatus.CHECKED_OUT) {
                return mapToResponse(existing);
            }
        }

        DailyAttendance attendance = existingOpt.orElseGet(DailyAttendance::new);
        attendance.setEmployee(currentUser);
        attendance.setAttendanceDate(today);
        attendance.setMorningCheckIn(LocalTime.now(IST_ZONE).truncatedTo(ChronoUnit.SECONDS));
        attendance.setWorkMode(WorkMode.WFH);
        attendance.setStatus(AttendanceStatus.WORKING);

        if (request != null && request.getLatitude() != null && request.getLongitude() != null) {
            attendance.setMorningLatitude(request.getLatitude());
            attendance.setMorningLongitude(request.getLongitude());
        }

        DailyAttendance saved = dailyAttendanceRepository.save(attendance);

        // Record initial WFH Location history if location provided
        if (request != null && request.getLatitude() != null && request.getLongitude() != null) {
            WfhLocationHistory loc = new WfhLocationHistory();
            loc.setEmployee(currentUser);
            loc.setAttendance(saved);
            loc.setWfhRequest(approvedWfh.get(0));
            loc.setLatitude(request.getLatitude());
            loc.setLongitude(request.getLongitude());
            loc.setCapturedAt(LocalDateTime.now(IST_ZONE));
            wfhLocationHistoryRepository.save(loc);
        }

        taskSnapshotService.captureTaskSnapshot(currentUser, saved, SnapshotType.MORNING);

        auditLogService.log(
                currentUser,
                AuditAction.LOGIN,
                "DailyAttendance",
                saved.getId(),
                "WFH Check-in completed at " + saved.getMorningCheckIn().format(TIME_FORMATTER)
        );

        return mapToResponse(saved);
    }

    @Transactional
    public AttendanceResponse endWorkDay(CheckOutRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now(IST_ZONE);

        DailyAttendance attendance = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, today)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No check-in record found for today. Please check in first."));

        if (attendance.getStatus() == AttendanceStatus.COMPLETED || attendance.getStatus() == AttendanceStatus.CHECKED_OUT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Your workday has already been ended today.");
        }

        LocalTime checkoutTime = LocalTime.now(IST_ZONE).truncatedTo(ChronoUnit.SECONDS);
        attendance.setEveningCheckOut(checkoutTime);
        attendance.setStatus(AttendanceStatus.COMPLETED);

        if (request != null && request.getLatitude() != null && request.getLongitude() != null) {
            attendance.setEveningLatitude(request.getLatitude());
            attendance.setEveningLongitude(request.getLongitude());
        }

        if (attendance.getMorningCheckIn() != null) {
            Duration duration = Duration.between(attendance.getMorningCheckIn(), checkoutTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            attendance.setOfficeDuration(String.format("%dh %dm", hours, minutes));
        }

        DailyAttendance saved = dailyAttendanceRepository.save(attendance);

        // Capture final location if available
        if (request != null && request.getLatitude() != null && request.getLongitude() != null && attendance.getWorkMode() == WorkMode.WFH) {
            WfhLocationHistory loc = new WfhLocationHistory();
            loc.setEmployee(currentUser);
            loc.setAttendance(saved);
            loc.setLatitude(request.getLatitude());
            loc.setLongitude(request.getLongitude());
            loc.setCapturedAt(LocalDateTime.now(IST_ZONE));
            wfhLocationHistoryRepository.save(loc);
        }

        taskSnapshotService.captureTaskSnapshot(currentUser, saved, SnapshotType.EVENING);

        auditLogService.log(
                currentUser,
                AuditAction.LOGOUT,
                "DailyAttendance",
                saved.getId(),
                "Work day ended at " + saved.getEveningCheckOut().format(TIME_FORMATTER)
        );

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public DailyAttendanceSummaryDto getTodayAttendance() {
        User currentUser = securityUtils.getCurrentUser();
        LocalDate today = LocalDate.now(IST_ZONE);

        Optional<DailyAttendance> attendanceOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(currentUser, today);

        if (attendanceOpt.isEmpty()) {
            return DailyAttendanceSummaryDto.builder()
                    .attendanceDate(today)
                    .status(AttendanceStatus.NOT_STARTED)
                    .morningTaskCount(0)
                    .tasksAdded(0)
                    .finalTaskCount(0)
                    .completedTaskCount(0)
                    .inProgressTaskCount(0)
                    .pendingTaskCount(0)
                    .build();
        }

        DailyAttendance attendance = attendanceOpt.get();
        return buildSummary(attendance, currentUser);
    }

    @Transactional(readOnly = true)
    public EmployeeDailyActivityResponse getEmployeeDailyActivity(Long employeeId, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now(IST_ZONE);
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employeeId));

        Optional<DailyAttendance> attendanceOpt = dailyAttendanceRepository.findByEmployeeAndAttendanceDate(employee, queryDate);

        AttendanceResponse attResp = attendanceOpt.map(this::mapToResponse).orElse(null);
        DailyAttendanceSummaryDto summary = attendanceOpt.map(att -> buildSummary(att, employee))
                .orElseGet(() -> DailyAttendanceSummaryDto.builder()
                        .attendanceDate(queryDate)
                        .status(AttendanceStatus.NOT_STARTED)
                        .build());

        List<TaskSnapshotDto> morningSnapshots = new ArrayList<>();
        List<TaskSnapshotDto> eveningSnapshots = new ArrayList<>();

        if (attendanceOpt.isPresent()) {
            DailyAttendance att = attendanceOpt.get();
            morningSnapshots = dailyTaskSnapshotRepository.findByAttendanceAndSnapshotType(att, SnapshotType.MORNING)
                    .stream().map(this::mapToSnapshotDto).collect(Collectors.toList());

            eveningSnapshots = dailyTaskSnapshotRepository.findByAttendanceAndSnapshotType(att, SnapshotType.EVENING)
                    .stream().map(this::mapToSnapshotDto).collect(Collectors.toList());

            // If evening snapshots are not frozen yet (user active during the shift before check-out),
            // construct current live snapshots from real-time tasks so completion and updates show live!
            if (eveningSnapshots.isEmpty()) {
                LocalDateTime now = LocalDateTime.now(IST_ZONE);
                eveningSnapshots = taskRepository.findByAssignedTo(employee).stream()
                        .map(t -> TaskSnapshotDto.builder()
                                .taskId(t.getId())
                                .taskTitle(t.getTitle())
                                .taskStatus(t.getStatus())
                                .taskProgress(t.getProgressPercentage() != null ? t.getProgressPercentage() : 0)
                                .snapshotTime(now)
                                .build())
                        .collect(Collectors.toList());
            }
        }

        TaskChangeDto taskChanges = calculateTaskChanges(morningSnapshots, eveningSnapshots);

        // Timeline
        LocalDateTime startOfDay = queryDate.atStartOfDay();
        LocalDateTime endOfDay = queryDate.atTime(LocalTime.MAX);
        List<TaskActivityTimelineDto> timeline = taskActivityHistoryRepository
                .findByEmployeeIdAndCreatedAtBetweenOrderByCreatedAtAsc(employeeId, startOfDay, endOfDay)
                .stream().map(this::mapToTimelineDto).collect(Collectors.toList());

        return EmployeeDailyActivityResponse.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .employeeCode(employee.getEmployeeId())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : "General")
                .attendanceDate(queryDate)
                .attendance(attResp)
                .summary(summary)
                .morningTasks(morningSnapshots)
                .eveningTasks(eveningSnapshots)
                .taskChanges(taskChanges)
                .timeline(timeline)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAdminAttendanceList(LocalDate date, Pageable pageable) {
        LocalDate queryDate = date != null ? date : LocalDate.now(IST_ZONE);

        // 1. Fetch all active employees
        List<User> activeEmployees = userRepository.findByStatus(UserStatus.ACTIVE);
        if (activeEmployees.isEmpty()) {
            activeEmployees = userRepository.findAll();
        }

        // 2. Fetch existing DailyAttendance records for queryDate
        List<DailyAttendance> attendanceList = dailyAttendanceRepository.findByAttendanceDate(queryDate);
        Map<Long, DailyAttendance> attendanceMap = attendanceList.stream()
                .filter(a -> a.getEmployee() != null)
                .collect(Collectors.toMap(a -> a.getEmployee().getId(), a -> a, (a1, a2) -> a1));

        // 3. Fetch LoginActivity for queryDate for fallback matching
        List<LoginActivity> loginActivities = loginActivityRepository.findByLoginDateOrderByLoginTimeDesc(queryDate);
        Map<Long, List<LoginActivity>> loginMap = loginActivities.stream()
                .filter(l -> l.getUser() != null)
                .collect(Collectors.groupingBy(l -> l.getUser().getId()));

        List<AttendanceResponse> responseList = new ArrayList<>();

        for (User emp : activeEmployees) {
            DailyAttendance att = attendanceMap.get(emp.getId());
            if (att != null) {
                responseList.add(mapToResponse(att));
            } else {
                List<LoginActivity> userLogins = loginMap.get(emp.getId());
                boolean isWfhApproved = !leaveRequestRepository.findApprovedWfhRequestOnDate(emp, queryDate).isEmpty();

                if (userLogins != null && !userLogins.isEmpty()) {
                    // Find earliest login time and latest logout time
                    LocalTime firstLogin = userLogins.stream()
                            .map(LoginActivity::getLoginTime)
                            .filter(Objects::nonNull)
                            .min(LocalTime::compareTo)
                            .orElse(null);

                    LocalTime lastLogout = userLogins.stream()
                            .map(LoginActivity::getLogoutTime)
                            .filter(Objects::nonNull)
                            .max(LocalTime::compareTo)
                            .orElse(null);

                    boolean hasActiveSession = userLogins.stream().anyMatch(l -> l.getStatus() == LoginStatus.ACTIVE);
                    AttendanceStatus status = hasActiveSession ? AttendanceStatus.CHECKED_IN : AttendanceStatus.CHECKED_OUT;

                    String duration = null;
                    if (firstLogin != null && lastLogout != null) {
                        Duration d = Duration.between(firstLogin, lastLogout);
                        duration = String.format("%dh %dm", d.toHours(), d.toMinutesPart());
                    } else if (firstLogin != null) {
                        Duration d = Duration.between(firstLogin, LocalTime.now(IST_ZONE));
                        duration = String.format("%dh %dm", d.toHours(), d.toMinutesPart());
                    }

                    responseList.add(AttendanceResponse.builder()
                            .id(null)
                            .employeeId(emp.getId())
                            .employeeName(emp.getName())
                            .employeeCode(emp.getEmployeeId())
                            .attendanceDate(queryDate)
                            .workMode(isWfhApproved ? WorkMode.WFH : WorkMode.OFFICE)
                            .isWfhApprovedToday(isWfhApproved)
                            .morningCheckIn(firstLogin)
                            .eveningCheckOut(lastLogout)
                            .status(status)
                            .officeDuration(duration)
                            .build());
                } else {
                    responseList.add(AttendanceResponse.builder()
                            .id(null)
                            .employeeId(emp.getId())
                            .employeeName(emp.getName())
                            .employeeCode(emp.getEmployeeId())
                            .attendanceDate(queryDate)
                            .workMode(isWfhApproved ? WorkMode.WFH : WorkMode.OFFICE)
                            .isWfhApprovedToday(isWfhApproved)
                            .morningCheckIn(null)
                            .eveningCheckOut(null)
                            .status(AttendanceStatus.NOT_STARTED)
                            .officeDuration(null)
                            .build());
                }
            }
        }

        // Apply manual pagination to the response list
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseList.size());
        List<AttendanceResponse> pageContent = (start <= responseList.size()) 
                ? responseList.subList(start, end) 
                : Collections.emptyList();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, responseList.size());
    }

    @Transactional
    public AttendanceResponse reopenAttendanceDay(Long attendanceId) {
        DailyAttendance attendance = dailyAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("DailyAttendance", "id", attendanceId));

        attendance.setStatus(AttendanceStatus.REOPENED);
        DailyAttendance saved = dailyAttendanceRepository.save(attendance);

        auditLogService.log(
                securityUtils.getCurrentUser(),
                AuditAction.EMPLOYEE_UPDATED,
                "DailyAttendance",
                saved.getId(),
                "Attendance reopened by Admin for employee ID: " + attendance.getEmployee().getEmployeeId()
        );

        return mapToResponse(saved);
    }

    private DailyAttendanceSummaryDto buildSummary(DailyAttendance attendance, User employee) {
        List<DailyTaskSnapshot> morningSnaps = dailyTaskSnapshotRepository.findByAttendanceAndSnapshotType(attendance, SnapshotType.MORNING);
        List<DailyTaskSnapshot> eveningSnaps = dailyTaskSnapshotRepository.findByAttendanceAndSnapshotType(attendance, SnapshotType.EVENING);

        int morningCount = morningSnaps.size();
        Set<Long> morningTaskIds = morningSnaps.stream().map(DailyTaskSnapshot::getTaskId).collect(Collectors.toSet());

        int completed = 0;
        int inProgress = 0;
        int pending = 0;
        int finalCount = 0;
        long added = 0;

        if (!eveningSnaps.isEmpty()) {
            // Checked out: use frozen evening snapshots
            finalCount = eveningSnaps.size();
            Set<Long> finalTaskIds = eveningSnaps.stream().map(DailyTaskSnapshot::getTaskId).collect(Collectors.toSet());
            added = finalTaskIds.stream().filter(id -> !morningTaskIds.contains(id)).count();

            for (DailyTaskSnapshot s : eveningSnaps) {
                if (s.getTaskStatus() == TaskStatus.COMPLETED) completed++;
                else if (s.getTaskStatus() == TaskStatus.IN_PROGRESS) inProgress++;
                else pending++;
            }
        } else {
            // Active shift (before check-out): calculate live current task metrics for today!
            List<Task> liveTasks = taskRepository.findByAssignedTo(employee);
            finalCount = liveTasks.size();
            Set<Long> liveTaskIds = liveTasks.stream().map(Task::getId).collect(Collectors.toSet());
            added = liveTaskIds.stream().filter(id -> !morningTaskIds.contains(id)).count();

            for (Task t : liveTasks) {
                if (t.getStatus() == TaskStatus.COMPLETED) completed++;
                else if (t.getStatus() == TaskStatus.IN_PROGRESS) inProgress++;
                else pending++;
            }
        }

        return DailyAttendanceSummaryDto.builder()
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus())
                .morningCheckIn(attendance.getMorningCheckIn())
                .morningLatitude(attendance.getMorningLatitude())
                .morningLongitude(attendance.getMorningLongitude())
                .morningDistanceFromOffice(attendance.getMorningDistanceFromOffice())
                .eveningCheckOut(attendance.getEveningCheckOut())
                .eveningLatitude(attendance.getEveningLatitude())
                .eveningLongitude(attendance.getEveningLongitude())
                .eveningDistanceFromOffice(attendance.getEveningDistanceFromOffice())
                .officeDuration(attendance.getOfficeDuration())
                .morningTaskCount(morningCount)
                .tasksAdded((int) added)
                .finalTaskCount(finalCount)
                .completedTaskCount(completed)
                .inProgressTaskCount(inProgress)
                .pendingTaskCount(pending)
                .build();
    }

    private TaskChangeDto calculateTaskChanges(List<TaskSnapshotDto> morning, List<TaskSnapshotDto> evening) {
        Set<Long> morningIds = morning.stream().map(TaskSnapshotDto::getTaskId).collect(Collectors.toSet());
        Map<Long, TaskSnapshotDto> morningMap = morning.stream().collect(Collectors.toMap(TaskSnapshotDto::getTaskId, dto -> dto, (a, b) -> a));

        List<TaskSnapshotDto> added = new ArrayList<>();
        List<TaskSnapshotDto> updated = new ArrayList<>();
        List<TaskSnapshotDto> completed = new ArrayList<>();

        for (TaskSnapshotDto eve : evening) {
            if (!morningIds.contains(eve.getTaskId())) {
                added.add(eve);
            } else {
                TaskSnapshotDto morn = morningMap.get(eve.getTaskId());
                if (morn != null && (!morn.getTaskStatus().equals(eve.getTaskStatus()) || !Objects.equals(morn.getTaskProgress(), eve.getTaskProgress()))) {
                    updated.add(eve);
                }
            }
            if (eve.getTaskStatus() == TaskStatus.COMPLETED) {
                completed.add(eve);
            }
        }

        return TaskChangeDto.builder()
                .addedTasks(added)
                .updatedTasks(updated)
                .completedTasks(completed)
                .build();
    }

    private AttendanceResponse mapToResponse(DailyAttendance a) {
        User emp = a.getEmployee();
        boolean isWfhApproved = false;
        if (emp != null) {
            LocalDate today = a.getAttendanceDate() != null ? a.getAttendanceDate() : LocalDate.now();
            isWfhApproved = !leaveRequestRepository.findApprovedWfhRequestOnDate(emp, today).isEmpty();
        }

        return AttendanceResponse.builder()
                .id(a.getId())
                .employeeId(emp != null ? emp.getId() : null)
                .employeeName(emp != null ? emp.getName() : "Employee")
                .employeeCode(emp != null ? emp.getEmployeeId() : null)
                .attendanceDate(a.getAttendanceDate())
                .workMode(a.getWorkMode() != null ? a.getWorkMode() : WorkMode.OFFICE)
                .isWfhApprovedToday(isWfhApproved)
                .morningCheckIn(a.getMorningCheckIn())
                .morningLatitude(a.getMorningLatitude())
                .morningLongitude(a.getMorningLongitude())
                .morningDistanceFromOffice(a.getMorningDistanceFromOffice())
                .eveningCheckOut(a.getEveningCheckOut())
                .eveningLatitude(a.getEveningLatitude())
                .eveningLongitude(a.getEveningLongitude())
                .eveningDistanceFromOffice(a.getEveningDistanceFromOffice())
                .status(a.getStatus())
                .officeDuration(a.getOfficeDuration())
                .build();
    }

    private TaskSnapshotDto mapToSnapshotDto(DailyTaskSnapshot s) {
        return TaskSnapshotDto.builder()
                .id(s.getId())
                .taskId(s.getTaskId())
                .taskTitle(s.getTaskTitle())
                .taskStatus(s.getTaskStatus())
                .taskProgress(s.getTaskProgress())
                .snapshotTime(s.getSnapshotTime())
                .build();
    }

    private TaskActivityTimelineDto mapToTimelineDto(TaskActivityHistory h) {
        return TaskActivityTimelineDto.builder()
                .id(h.getId())
                .taskId(h.getTask() != null ? h.getTask().getId() : null)
                .taskTitle(h.getTask() != null ? h.getTask().getTitle() : "Task")
                .actionType(h.getActionType())
                .oldStatus(h.getOldStatus())
                .newStatus(h.getNewStatus())
                .oldProgress(h.getOldProgress())
                .newProgress(h.getNewProgress())
                .description(h.getDescription())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
