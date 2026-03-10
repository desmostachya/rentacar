package com.kush.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long paymentId;
    private String paymentReference;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime createdAt;
}
