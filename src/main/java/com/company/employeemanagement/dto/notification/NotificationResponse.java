package com.company.employeemanagement.dto.notification;

import com.company.employeemanagement.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private Long referenceId;
    private String referenceType;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static NotificationResponseBuilder builder() { return new NotificationResponseBuilder(); }

    public static class NotificationResponseBuilder {
        private final NotificationResponse r = new NotificationResponse();

        public NotificationResponseBuilder id(Long v) { r.id = v; return this; }
        public NotificationResponseBuilder type(NotificationType v) { r.type = v; return this; }
        public NotificationResponseBuilder title(String v) { r.title = v; return this; }
        public NotificationResponseBuilder message(String v) { r.message = v; return this; }
        public NotificationResponseBuilder isRead(Boolean v) { r.isRead = v; return this; }
        public NotificationResponseBuilder referenceId(Long v) { r.referenceId = v; return this; }
        public NotificationResponseBuilder referenceType(String v) { r.referenceType = v; return this; }
        public NotificationResponseBuilder createdAt(LocalDateTime v) { r.createdAt = v; return this; }

        public NotificationResponse build() { return r; }
    }
}
