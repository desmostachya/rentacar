package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.PaymentDTO;
import com.kush.dto.ProcessPaymentRequest;
import com.kush.entity.Payment;
import com.kush.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Processing payment: {}", request.getPaymentMethod());
        PaymentDTO paymentDTO = paymentService.processPayment(request);
        
        ApiResponse<PaymentDTO> response = ApiResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment processed successfully")
                .data(paymentDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable Long id) {
        log.info("Fetching payment: {}", id);
        PaymentDTO paymentDTO = paymentService.getPaymentById(id);
        
        ApiResponse<PaymentDTO> response = ApiResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(paymentDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllPayments() {
        log.info("Fetching all payments");
        List<PaymentDTO> payments = paymentService.getAllPayments();
        
        ApiResponse<List<PaymentDTO>> response = ApiResponse.<List<PaymentDTO>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(payments)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-payments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getMyPayments() {
        log.info("Fetching current user payments");
        List<PaymentDTO> payments = paymentService.getUserPayments();
        
        ApiResponse<List<PaymentDTO>> response = ApiResponse.<List<PaymentDTO>>builder()
                .success(true)
                .message("User payments retrieved successfully")
                .data(payments)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByStatus(@PathVariable String status) {
        log.info("Fetching payments by status: {}", status);
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(Payment.PaymentStatus.valueOf(status));
        
        ApiResponse<List<PaymentDTO>> response = ApiResponse.<List<PaymentDTO>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(payments)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PaymentDTO>> refundPayment(@PathVariable Long id) {
        log.info("Refunding payment: {}", id);
        PaymentDTO paymentDTO = paymentService.refundPayment(id);
        
        ApiResponse<PaymentDTO> response = ApiResponse.<PaymentDTO>builder()
                .success(true)
                .message("Payment refunded successfully")
                .data(paymentDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
