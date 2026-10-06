package com.company.employeemanagement.service;

import com.company.employeemanagement.entity.AuditAction;
import com.company.employeemanagement.entity.User;

public interface AuditLogService {
    void log(User actor, AuditAction action, String entityType, Long entityId, String description);
    void log(User actor, AuditAction action, String entityType, Long entityId, String description, String oldValue, String newValue);
}
