package com.company.employeemanagement.service;

import com.company.employeemanagement.entity.NotificationType;
import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.dto.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createNotification(User recipient, NotificationType type, String title, String message, Long referenceId, String referenceType);
    Page<NotificationResponse> getMyNotifications(Pageable pageable);
    long getUnreadCount();
    void markAllRead();
    void markRead(Long id);
}
