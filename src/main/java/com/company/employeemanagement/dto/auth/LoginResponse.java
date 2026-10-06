package com.company.employeemanagement.dto.auth;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    public LoginResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public static LoginResponseBuilder builder() { return new LoginResponseBuilder(); }

    public static class LoginResponseBuilder {
        private final LoginResponse r = new LoginResponse();

        public LoginResponseBuilder success(boolean v) { r.success = v; return this; }
        public LoginResponseBuilder message(String v) { r.message = v; return this; }
        public LoginResponseBuilder token(String v) { r.token = v; return this; }
        public LoginResponseBuilder tokenType(String v) { r.tokenType = v; return this; }
        public LoginResponseBuilder expiresIn(long v) { r.expiresIn = v; return this; }
        public LoginResponseBuilder user(UserInfo v) { r.user = v; return this; }

        public LoginResponse build() { return r; }
    }
}
