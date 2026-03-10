package com.kush.repository;

import com.kush.entity.Insurance;
import com.kush.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    Optional<Insurance> findByPolicyNumber(String policyNumber);
    List<Insurance> findByVehicle(Vehicle vehicle);
    List<Insurance> findByStatus(Insurance.InsuranceStatus status);
}
