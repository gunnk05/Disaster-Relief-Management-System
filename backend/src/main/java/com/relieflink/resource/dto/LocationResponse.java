package com.relieflink.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String name;
    private String region;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String weatherCondition;
    private String weatherAlert;
    private LocalDateTime lastWeatherUpdate;
}
