package com.company.employeemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "login_activity", indexes = {
        @Index(name = "idx_login_user", columnList = "user_id"),
        @Index(name = "idx_login_date", columnList = "login_date"),
        @Index(name = "idx_login_status", columnList = "status")
})
public class LoginActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_date", nullable = false)
    private LocalDate loginDate;

    @Column(name = "login_time", nullable = false)
    private LocalTime loginTime;

    @Column(name = "logout_time")
    private LocalTime logoutTime;

    @Column(name = "session_duration", length = 20)
    private String sessionDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoginStatus status = LoginStatus.ACTIVE;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "is_office_location")
    private Boolean isOfficeLocation = true;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_name", length = 255)
    private String locationName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public LoginActivity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getLoginDate() { return loginDate; }
    public void setLoginDate(LocalDate loginDate) { this.loginDate = loginDate; }

    public LocalTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalTime loginTime) { this.loginTime = loginTime; }

    public LocalTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalTime logoutTime) { this.logoutTime = logoutTime; }

    public String getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(String sessionDuration) { this.sessionDuration = sessionDuration; }

    public LoginStatus getStatus() { return status; }
    public void setStatus(LoginStatus status) { this.status = status; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Boolean getIsOfficeLocation() { return isOfficeLocation; }
    public void setIsOfficeLocation(Boolean isOfficeLocation) { this.isOfficeLocation = isOfficeLocation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static LoginActivityBuilder builder() { return new LoginActivityBuilder(); }

    public static class LoginActivityBuilder {
        private final LoginActivity la = new LoginActivity();

        public LoginActivityBuilder id(Long id) { la.id = id; return this; }
        public LoginActivityBuilder user(User user) { la.user = user; return this; }
        public LoginActivityBuilder loginDate(LocalDate loginDate) { la.loginDate = loginDate; return this; }
        public LoginActivityBuilder loginTime(LocalTime loginTime) { la.loginTime = loginTime; return this; }
        public LoginActivityBuilder logoutTime(LocalTime logoutTime) { la.logoutTime = logoutTime; return this; }
        public LoginActivityBuilder sessionDuration(String sessionDuration) { la.sessionDuration = sessionDuration; return this; }
        public LoginActivityBuilder status(LoginStatus status) { la.status = status; return this; }
        public LoginActivityBuilder ipAddress(String ipAddress) { la.ipAddress = ipAddress; return this; }
        public LoginActivityBuilder isOfficeLocation(Boolean isOfficeLocation) { la.isOfficeLocation = isOfficeLocation; return this; }
        public LoginActivityBuilder latitude(Double latitude) { la.latitude = latitude; return this; }
        public LoginActivityBuilder longitude(Double longitude) { la.longitude = longitude; return this; }
        public LoginActivityBuilder locationName(String locationName) { la.locationName = locationName; return this; }
        public LoginActivityBuilder createdAt(LocalDateTime createdAt) { la.createdAt = createdAt; return this; }

        public LoginActivity build() { return la; }
    }
}
