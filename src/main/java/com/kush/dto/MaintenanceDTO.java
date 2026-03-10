package com.kush.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {
    private Long maintenanceId;
    private String maintenanceType;
    private LocalDate maintenanceDate;
    private LocalDate completionDate;
    private String description;
    private String notes;
    private BigDecimal cost;
    private String status;
    private Long vehicleId;
    private Long mileageAtService;
    private Long nextServiceMileage;
    private LocalDateTime createdAt;
}
