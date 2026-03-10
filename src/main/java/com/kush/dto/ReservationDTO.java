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
public class ReservationDTO {
    private Long reservationId;
    private String reservationNumber;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private String status;
    private BigDecimal totalCost;
    private String pickupLocation;
    private String returnLocation;
    private String specialRequests;
    private String paymentStatus;
    private Long vehicleId;
    private String vehicleInfo;
    private Long userId;
    private String customerName;
    private LocalDateTime createdAt;
}
