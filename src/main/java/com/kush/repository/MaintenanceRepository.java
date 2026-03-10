package com.kush.repository;

import com.kush.entity.Maintenance;
import com.kush.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByVehicle(Vehicle vehicle);
    List<Maintenance> findByStatus(Maintenance.MaintenanceStatus status);
}
