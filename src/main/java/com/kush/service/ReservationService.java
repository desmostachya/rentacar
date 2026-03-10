package com.kush.service;

import com.kush.dto.CreateReservationRequest;
import com.kush.dto.ReservationDTO;
import com.kush.entity.Reservation;
import com.kush.entity.User;
import com.kush.entity.Vehicle;
import com.kush.exception.InvalidOperationException;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.ReservationRepository;
import com.kush.repository.UserRepository;
import com.kush.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                            VehicleRepository vehicleRepository,
                            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public ReservationDTO createReservation(CreateReservationRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new InvalidOperationException("Vehicle is not available for reservation");
        }

        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
                vehicle, request.getPickupDate(), request.getReturnDate());

        if (!conflictingReservations.isEmpty()) {
            throw new InvalidOperationException("Vehicle has conflicting reservations for the selected dates");
        }

        User currentUser = getCurrentUser();

        long days = ChronoUnit.DAYS.between(request.getPickupDate(), request.getReturnDate());
        BigDecimal totalCost = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));

        Reservation reservation = Reservation.builder()
                .reservationNumber(generateReservationNumber())
                .pickupDate(request.getPickupDate())
                .returnDate(request.getReturnDate())
                .status(Reservation.ReservationStatus.PENDING)
                .totalCost(totalCost)
                .pickupLocation(request.getPickupLocation())
                .returnLocation(request.getReturnLocation())
                .specialRequests(request.getSpecialRequests())
                .user(currentUser)
                .vehicle(vehicle)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        vehicle.setStatus(Vehicle.VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);

        log.info("Reservation created: {} for user: {}", savedReservation.getReservationNumber(), currentUser.getEmail());

        return mapToDTO(savedReservation);
    }

    public ReservationDTO getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return mapToDTO(reservation);
    }

    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getUserReservations() {
        User currentUser = getCurrentUser();
        return reservationRepository.findByUser(currentUser).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getReservationsByStatus(Reservation.ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO updateReservationStatus(Long reservationId, Reservation.ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("Reservation status updated: {} -> {}", reservation.getReservationNumber(), status);

        return mapToDTO(updatedReservation);
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED ||
            reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot cancel a completed or already cancelled reservation");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());

        Vehicle vehicle = reservation.getVehicle();
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        Reservation cancelledReservation = reservationRepository.save(reservation);
        log.info("Reservation cancelled: {}", reservation.getReservationNumber());

        return mapToDTO(cancelledReservation);
    }

    public ReservationDTO confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new InvalidOperationException("Only pending reservations can be confirmed");
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation confirmedReservation = reservationRepository.save(reservation);
        log.info("Reservation confirmed: {}", reservation.getReservationNumber());

        return mapToDTO(confirmedReservation);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private String generateReservationNumber() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ReservationDTO mapToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .reservationId(reservation.getReservationId())
                .reservationNumber(reservation.getReservationNumber())
                .pickupDate(reservation.getPickupDate())
                .returnDate(reservation.getReturnDate())
                .status(reservation.getStatus().name())
                .totalCost(reservation.getTotalCost())
                .pickupLocation(reservation.getPickupLocation())
                .returnLocation(reservation.getReturnLocation())
                .specialRequests(reservation.getSpecialRequests())
                .paymentStatus(reservation.getPaymentStatus().name())
                .vehicleId(reservation.getVehicle().getVehicleId())
                .vehicleInfo(reservation.getVehicle().getMake() + " " + reservation.getVehicle().getModel())
                .userId(reservation.getUser().getUserId())
                .customerName(reservation.getUser().getFullName())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
