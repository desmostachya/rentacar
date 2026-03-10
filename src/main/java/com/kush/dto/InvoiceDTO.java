package com.kush.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private Long invoiceId;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private String description;
    private String status;
    private Long reservationId;
    private LocalDateTime createdAt;
}
