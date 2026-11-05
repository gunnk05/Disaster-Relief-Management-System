package com.relieflink.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.relieflink.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code audit_log} table.
 *
 * <p>Records system-level actions (request approvals, assignments, etc.)
 * for administrative review.</p>
 */
@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    @JsonIgnoreProperties({"password", "authorities", "accountNonExpired",
                            "accountNonLocked", "credentialsNonExpired", "enabled",
                            "isRequestingHelp", "hibernateLazyInitializer", "handler"})
    private User performedBy;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
