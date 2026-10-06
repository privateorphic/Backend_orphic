package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.dto.notification.NotificationResponse;
import com.company.employeemanagement.entity.Notification;
import com.company.employeemanagement.entity.NotificationType;
import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.repository.NotificationRepository;
import com.company.employeemanagement.security.SecurityUtils;
import com.company.employeemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public void createNotification(User recipient, NotificationType type, String title, String message,
                                   Long referenceId, String referenceType) {
        try {
            Notification notification = Notification.builder()
                    .user(recipient)
                    .type(type)
                    .title(title)
                    .message(message)
                    .isRead(false)
                    .referenceId(referenceId)
                    .referenceType(referenceType)
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(NotificationServiceImpl.class)
                    .error("Failed to create notification for user {}: {}", recipient != null ? recipient.getEmployeeId() : "null", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User user = securityUtils.getCurrentUser();
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAllRead() {
        User user = securityUtils.getCurrentUser();
        notificationRepository.markAllReadForUser(user.getId());
    }

    @Override
    @Transactional
    public void markRead(Long id) {
        User user = securityUtils.getCurrentUser();
        notificationRepository.markReadById(id, user.getId());
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.getIsRead())
                .referenceId(n.getReferenceId())
                .referenceType(n.getReferenceType())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
