package com.relieflink.resource;

import com.relieflink.common.enums.WeatherAlert;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code locations} table.
 *
 * <p>Represents a geographic area affected by a disaster, including
 * coordinates and current weather status.</p>
 */
@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String region;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "weather_condition", length = 50)
    private String weatherCondition;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_alert", columnDefinition = "ENUM('none','yellow','orange','red') DEFAULT 'none'")
    private WeatherAlert weatherAlert;

    @Column(name = "last_weather_update")
    private LocalDateTime lastWeatherUpdate;
}
