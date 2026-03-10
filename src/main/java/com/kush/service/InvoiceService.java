package com.kush.service;

import com.kush.dto.InvoiceDTO;
import com.kush.entity.Invoice;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public InvoiceDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return mapToDTO(invoice);
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .totalAmount(invoice.getTotalAmount())
                .discount(invoice.getDiscount())
                .description(invoice.getDescription())
                .status(invoice.getStatus().name())
                .reservationId(invoice.getReservation().getReservationId())
                .createdAt(invoice.getCreatedAt())
                .build();
    }
}
