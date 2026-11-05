package com.relieflink.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ResourceAuditLogRepository resourceAuditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository,
                            ResourceAuditLogRepository resourceAuditLogRepository) {
        this.auditLogRepository = auditLogRepository;
        this.resourceAuditLogRepository = resourceAuditLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceAuditLog> getResourceAuditLogs() {
        return resourceAuditLogRepository.findAllByOrderByChangedAtDesc();
    }
}
