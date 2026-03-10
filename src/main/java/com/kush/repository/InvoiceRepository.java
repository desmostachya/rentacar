package com.kush.repository;

import com.kush.entity.Invoice;
import com.kush.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findByReservation(Reservation reservation);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
}
