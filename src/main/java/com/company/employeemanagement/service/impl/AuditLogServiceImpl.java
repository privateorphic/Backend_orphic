package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.entity.AuditAction;
import com.company.employeemanagement.entity.AuditLog;
import com.company.employeemanagement.entity.User;
import com.company.employeemanagement.repository.AuditLogRepository;
import com.company.employeemanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(User actor, AuditAction action, String entityType, Long entityId, String description) {
        log(actor, action, entityType, entityId, description, null, null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(User actor, AuditAction action, String entityType, Long entityId,
                    String description, String oldValue, String newValue) {
        try {
            AuditLog log = AuditLog.builder()
                    .user(actor)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuditLogServiceImpl.class)
                    .error("Failed to save audit log action {}: {}", action, e.getMessage());
        }
    }
}
