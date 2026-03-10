package com.kush.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentReference;
}
