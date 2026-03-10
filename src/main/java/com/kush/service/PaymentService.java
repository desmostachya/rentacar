package com.kush.service;

import com.kush.dto.PaymentDTO;
import com.kush.dto.ProcessPaymentRequest;
import com.kush.entity.Payment;
import com.kush.entity.User;
import com.kush.exception.InvalidOperationException;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.PaymentRepository;
import com.kush.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public PaymentDTO processPayment(ProcessPaymentRequest request) {
        if (request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Payment amount must be greater than zero");
        }

        User currentUser = getCurrentUser();

        Payment payment = Payment.builder()
                .paymentReference(generatePaymentReference())
                .amount(request.getAmount())
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(Payment.PaymentStatus.COMPLETED)
                .transactionId(UUID.randomUUID().toString())
                .user(currentUser)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed: {} - Amount: {}", savedPayment.getPaymentReference(), request.getAmount());

        return mapToDTO(savedPayment);
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return mapToDTO(payment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getUserPayments() {
        User currentUser = getCurrentUser();
        return paymentRepository.findByUser(currentUser).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getPaymentStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new InvalidOperationException("Only completed payments can be refunded");
        }

        payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
        Payment refundedPayment = paymentRepository.save(payment);

        log.info("Payment refunded: {}", payment.getPaymentReference());

        return mapToDTO(refundedPayment);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .paymentReference(payment.getPaymentReference())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
