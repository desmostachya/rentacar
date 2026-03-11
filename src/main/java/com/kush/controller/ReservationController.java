package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.CreateReservationRequest;
import com.kush.dto.ReservationDTO;
import com.kush.entity.Reservation;
import com.kush.service.ReservationService;
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
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        log.info("Creating reservation for vehicle: {}", request.getVehicleId());
        ReservationDTO reservationDTO = reservationService.createReservation(request);
        
        ApiResponse<ReservationDTO> response = ApiResponse.<ReservationDTO>builder()
                .success(true)
                .message("Reservation created successfully")
                .data(reservationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationDTO>> getReservationById(@PathVariable Long id) {
        log.info("Fetching reservation: {}", id);
        ReservationDTO reservationDTO = reservationService.getReservationById(id);
        
        ApiResponse<ReservationDTO> response = ApiResponse.<ReservationDTO>builder()
                .success(true)
                .message("Reservation retrieved successfully")
                .data(reservationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getAllReservations() {
        log.info("Fetching all reservations");
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        
        ApiResponse<List<ReservationDTO>> response = ApiResponse.<List<ReservationDTO>>builder()
                .success(true)
                .message("Reservations retrieved successfully")
                .data(reservations)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-reservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getMyReservations() {
        log.info("Fetching current user reservations");
        List<ReservationDTO> reservations = reservationService.getUserReservations();
        
        ApiResponse<List<ReservationDTO>> response = ApiResponse.<List<ReservationDTO>>builder()
                .success(true)
                .message("User reservations retrieved successfully")
                .data(reservations)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getReservationsByStatus(@PathVariable String status) {
        log.info("Fetching reservations by status: {}", status);
        List<ReservationDTO> reservations = reservationService.getReservationsByStatus(Reservation.ReservationStatus.valueOf(status));
        
        ApiResponse<List<ReservationDTO>> response = ApiResponse.<List<ReservationDTO>>builder()
                .success(true)
                .message("Reservations retrieved successfully")
                .data(reservations)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<ApiResponse<ReservationDTO>> confirmReservation(@PathVariable Long id) {
        log.info("Confirming reservation: {}", id);
        ReservationDTO reservationDTO = reservationService.confirmReservation(id);
        
        ApiResponse<ReservationDTO> response = ApiResponse.<ReservationDTO>builder()
                .success(true)
                .message("Reservation confirmed successfully")
                .data(reservationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(@PathVariable Long id) {
        log.info("Cancelling reservation: {}", id);
        ReservationDTO reservationDTO = reservationService.cancelReservation(id);
        
        ApiResponse<ReservationDTO> response = ApiResponse.<ReservationDTO>builder()
                .success(true)
                .message("Reservation cancelled successfully")
                .data(reservationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<ReservationDTO>> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Updating reservation status: {} -> {}", id, status);
        ReservationDTO reservationDTO = reservationService.updateReservationStatus(id, Reservation.ReservationStatus.valueOf(status));
        
        ApiResponse<ReservationDTO> response = ApiResponse.<ReservationDTO>builder()
                .success(true)
                .message("Reservation status updated successfully")
                .data(reservationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
