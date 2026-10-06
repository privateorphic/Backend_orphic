package com.company.employeemanagement.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username or Employee ID is required")
    @JsonAlias({"identifier", "email", "employeeId"})
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private Boolean isOfficeLocation = true;
    private Double latitude;
    private Double longitude;
    private String locationName;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIdentifier() { return username; }
    public void setIdentifier(String identifier) { this.username = identifier; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getIsOfficeLocation() { return isOfficeLocation; }
    public void setIsOfficeLocation(Boolean isOfficeLocation) { this.isOfficeLocation = isOfficeLocation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public static LoginRequestBuilder builder() { return new LoginRequestBuilder(); }

    public static class LoginRequestBuilder {
        private final LoginRequest r = new LoginRequest();
        public LoginRequestBuilder username(String v) { r.username = v; return this; }
        public LoginRequestBuilder identifier(String v) { r.username = v; return this; }
        public LoginRequestBuilder password(String v) { r.password = v; return this; }
        public LoginRequestBuilder isOfficeLocation(Boolean v) { r.isOfficeLocation = v; return this; }
        public LoginRequestBuilder latitude(Double v) { r.latitude = v; return this; }
        public LoginRequestBuilder longitude(Double v) { r.longitude = v; return this; }
        public LoginRequestBuilder locationName(String v) { r.locationName = v; return this; }
        public LoginRequest build() { return r; }
    }
}
