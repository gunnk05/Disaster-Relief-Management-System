package com.relieflink.request;

import com.relieflink.common.enums.VolunteerRequestStatus;
import com.relieflink.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code volunteer_requests} table.
 *
 * <p>Tracks the assignment of a volunteer to a citizen's request.</p>
 */
@Entity
@Table(name = "volunteer_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('assigned','in_progress','completed','verified') DEFAULT 'assigned'")
    private VolunteerRequestStatus status;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
