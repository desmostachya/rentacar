package com.kush.repository;

import com.kush.entity.Location;
import com.kush.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);
    List<Vehicle> findByCategory(Vehicle.VehicleCategory category);
    List<Vehicle> findByLocation(Location location);
    
    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.category = :category")
    List<Vehicle> findAvailableByCategory(@Param("category") Vehicle.VehicleCategory category);
}
