package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.InvoiceDTO;
import com.kush.entity.Invoice;
import com.kush.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable Long id) {
        log.info("Fetching invoice: {}", id);
        InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(id);
        
        ApiResponse<InvoiceDTO> response = ApiResponse.<InvoiceDTO>builder()
                .success(true)
                .message("Invoice retrieved successfully")
                .data(invoiceDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAllInvoices() {
        log.info("Fetching all invoices");
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        
        ApiResponse<List<InvoiceDTO>> response = ApiResponse.<List<InvoiceDTO>>builder()
                .success(true)
                .message("Invoices retrieved successfully")
                .data(invoices)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByStatus(@PathVariable String status) {
        log.info("Fetching invoices by status: {}", status);
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(Invoice.InvoiceStatus.valueOf(status));
        
        ApiResponse<List<InvoiceDTO>> response = ApiResponse.<List<InvoiceDTO>>builder()
                .success(true)
                .message("Invoices retrieved successfully")
                .data(invoices)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
