package com.relieflink.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRequestDto {

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Quantity is required")
    private Integer quantityRequested;

    private String remarks;
}
