package com.relieflink.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationRequest {

    @NotBlank(message = "Location name is required")
    private String name;

    private String region;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String weatherCondition;
    private String weatherAlert;
}
