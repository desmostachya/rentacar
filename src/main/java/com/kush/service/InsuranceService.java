package com.kush.service;

import com.kush.entity.Insurance;
import com.kush.entity.Vehicle;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.InsuranceRepository;
import com.kush.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final VehicleRepository vehicleRepository;

    public InsuranceService(InsuranceRepository insuranceRepository, VehicleRepository vehicleRepository) {
        this.insuranceRepository = insuranceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Insurance createInsurance(Insurance insurance) {
        Insurance savedInsurance = insuranceRepository.save(insurance);
        log.info("Insurance created: {}", savedInsurance.getPolicyNumber());
        return savedInsurance;
    }

    public Insurance getInsuranceById(Long insuranceId) {
        return insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));
    }

    public List<Insurance> getAllInsurance() {
        return insuranceRepository.findAll();
    }

    public List<Insurance> getInsuranceByVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        return insuranceRepository.findByVehicle(vehicle);
    }

    public List<Insurance> getActiveInsurance() {
        return insuranceRepository.findByStatus(Insurance.InsuranceStatus.ACTIVE);
    }

    public Insurance updateInsurance(Long insuranceId, Insurance insuranceDetails) {
        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));

        insurance.setProvider(insuranceDetails.getProvider());
        insurance.setInsuranceType(insuranceDetails.getInsuranceType());
        insurance.setCoverageAmount(insuranceDetails.getCoverageAmount());
        insurance.setPremiumAmount(insuranceDetails.getPremiumAmount());
        insurance.setStartDate(insuranceDetails.getStartDate());
        insurance.setEndDate(insuranceDetails.getEndDate());
        insurance.setStatus(insuranceDetails.getStatus());
        insurance.setUpdatedAt(LocalDateTime.now());

        Insurance updatedInsurance = insuranceRepository.save(insurance);
        log.info("Insurance updated: {}", updatedInsurance.getPolicyNumber());

        return updatedInsurance;
    }

    public void deleteInsurance(Long insuranceId) {
        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));

        insuranceRepository.delete(insurance);
        log.info("Insurance deleted: {}", insurance.getPolicyNumber());
    }
}
