package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "daily_attendance", uniqueConstraints = {
        @UniqueConstraint(name = "uk_emp_attendance_date", columnNames = {"employee_id", "attendance_date"})
}, indexes = {
        @Index(name = "idx_attendance_emp", columnList = "employee_id"),
        @Index(name = "idx_attendance_date", columnList = "attendance_date"),
        @Index(name = "idx_attendance_status", columnList = "status")
})
public class DailyAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "morning_check_in")
    private LocalTime morningCheckIn;

    @Column(name = "morning_latitude")
    private Double morningLatitude;

    @Column(name = "morning_longitude")
    private Double morningLongitude;

    @Column(name = "morning_distance_from_office")
    private Double morningDistanceFromOffice;

    @Column(name = "evening_check_out")
    private LocalTime eveningCheckOut;

    @Column(name = "evening_latitude")
    private Double eveningLatitude;

    @Column(name = "evening_longitude")
    private Double eveningLongitude;

    @Column(name = "evening_distance_from_office")
    private Double eveningDistanceFromOffice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_location_id")
    private OfficeLocation officeLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", nullable = false, length = 20)
    private WorkMode workMode = WorkMode.OFFICE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AttendanceStatus status = AttendanceStatus.NOT_STARTED;

    @Column(name = "office_duration", length = 50)
    private String officeDuration;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DailyAttendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public LocalTime getMorningCheckIn() { return morningCheckIn; }
    public void setMorningCheckIn(LocalTime morningCheckIn) { this.morningCheckIn = morningCheckIn; }

    public Double getMorningLatitude() { return morningLatitude; }
    public void setMorningLatitude(Double morningLatitude) { this.morningLatitude = morningLatitude; }

    public Double getMorningLongitude() { return morningLongitude; }
    public void setMorningLongitude(Double morningLongitude) { this.morningLongitude = morningLongitude; }

    public Double getMorningDistanceFromOffice() { return morningDistanceFromOffice; }
    public void setMorningDistanceFromOffice(Double morningDistanceFromOffice) { this.morningDistanceFromOffice = morningDistanceFromOffice; }

    public LocalTime getEveningCheckOut() { return eveningCheckOut; }
    public void setEveningCheckOut(LocalTime eveningCheckOut) { this.eveningCheckOut = eveningCheckOut; }

    public Double getEveningLatitude() { return eveningLatitude; }
    public void setEveningLatitude(Double eveningLatitude) { this.eveningLatitude = eveningLatitude; }

    public Double getEveningLongitude() { return eveningLongitude; }
    public void setEveningLongitude(Double eveningLongitude) { this.eveningLongitude = eveningLongitude; }

    public Double getEveningDistanceFromOffice() { return eveningDistanceFromOffice; }
    public void setEveningDistanceFromOffice(Double eveningDistanceFromOffice) { this.eveningDistanceFromOffice = eveningDistanceFromOffice; }

    public OfficeLocation getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(OfficeLocation officeLocation) { this.officeLocation = officeLocation; }

    public WorkMode getWorkMode() { return workMode; }
    public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getOfficeDuration() { return officeDuration; }
    public void setOfficeDuration(String officeDuration) { this.officeDuration = officeDuration; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
