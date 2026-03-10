package com.kush.repository;

import com.kush.entity.Reservation;
import com.kush.entity.User;
import com.kush.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    List<Reservation> findByUser(User user);
    List<Reservation> findByVehicle(Vehicle vehicle);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.vehicle = :vehicle " +
           "AND r.status IN ('PENDING', 'CONFIRMED', 'ACTIVE') " +
           "AND ((r.pickupDate >= :startDate AND r.pickupDate <= :endDate) " +
           "OR (r.returnDate >= :startDate AND r.returnDate <= :endDate))")
    List<Reservation> findConflictingReservations(@Param("vehicle") Vehicle vehicle, 
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}
