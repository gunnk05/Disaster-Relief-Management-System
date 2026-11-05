package com.relieflink.request;

import com.relieflink.common.enums.RequestStatus;
import com.relieflink.resource.Location;
import com.relieflink.resource.Resource;
import com.relieflink.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code requests} table.
 *
 * <p>Represents a citizen's request for relief resources. Follows the
 * lifecycle: {@code pending → assigned → completed → fulfilled}.</p>
 */
@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "quantity_requested")
    private Integer quantityRequested;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('pending','assigned','completed','fulfilled') DEFAULT 'pending'")
    private RequestStatus status;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
