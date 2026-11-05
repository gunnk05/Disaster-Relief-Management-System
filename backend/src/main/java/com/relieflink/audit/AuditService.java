package com.relieflink.audit;

import java.util.List;

public interface AuditService {

    List<AuditLog> getAuditLogs();

    List<ResourceAuditLog> getResourceAuditLogs();
}
