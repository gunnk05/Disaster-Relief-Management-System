package com.relieflink.shelter;

import com.relieflink.resource.Location;
import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity mapping the {@code shelters} table.
 *
 * <p>Represents a relief camp or shelter with capacity tracking.</p>
 */
@Entity
@Table(name = "shelters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Integer capacity;

    @Column(name = "current_occupancy")
    private Integer currentOccupancy;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;
}
