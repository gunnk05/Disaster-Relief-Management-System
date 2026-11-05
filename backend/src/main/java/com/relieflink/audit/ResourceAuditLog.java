package com.relieflink.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code resource_audit_log} table.
 *
 * <p>Automatically populated by a MySQL trigger whenever a resource
 * is updated, recording old and new values for audit trail.</p>
 */
@Entity
@Table(name = "resource_audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "old_name", length = 100)
    private String oldName;

    @Column(name = "old_quantity")
    private Integer oldQuantity;

    @Column(name = "new_name", length = 100)
    private String newName;

    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Column(name = "action_type", length = 10)
    private String actionType;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;
}
