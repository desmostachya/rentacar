package com.kush.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationRequest {
    private Long vehicleId;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private String pickupLocation;
    private String returnLocation;
    private String specialRequests;
    private Boolean includeInsurance;
}
