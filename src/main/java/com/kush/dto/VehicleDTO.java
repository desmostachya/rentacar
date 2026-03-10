package com.kush.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Long vehicleId;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
    private String category;
    private String color;
    private Long mileage;
    private BigDecimal dailyRate;
    private String status;
    private String fuelType;
    private Integer passengerCapacity;
    private Boolean hasAirConditioning;
    private Boolean hasAutomaticTransmission;
    private String description;
    private String locationName;
    private LocalDateTime createdAt;
}

