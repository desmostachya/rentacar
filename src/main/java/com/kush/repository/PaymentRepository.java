package com.kush.repository;

import com.kush.entity.Payment;
import com.kush.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    List<Payment> findByUser(User user);
    List<Payment> findByPaymentStatus(Payment.PaymentStatus status);
}
