package com.relieflink.audit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditService.getAuditLogs());
    }

    @GetMapping("/resource-audit-logs")
    public ResponseEntity<List<ResourceAuditLog>> getResourceAuditLogs() {
        return ResponseEntity.ok(auditService.getResourceAuditLogs());
    }
}
