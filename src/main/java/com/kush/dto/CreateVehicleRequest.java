package com.kush.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
    private String category;
    private String color;
    private Long mileage;
    private BigDecimal dailyRate;
    private Integer fuelCapacity;
    private String fuelType;
    private Integer passengerCapacity;
    private Boolean hasAirConditioning;
    private Boolean hasAutomaticTransmission;
    private String description;
    private Long locationId;
}
