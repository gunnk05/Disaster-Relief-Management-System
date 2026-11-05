package com.relieflink.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAuditLogRepository extends JpaRepository<ResourceAuditLog, Long> {

    List<ResourceAuditLog> findAllByOrderByChangedAtDesc();
}
