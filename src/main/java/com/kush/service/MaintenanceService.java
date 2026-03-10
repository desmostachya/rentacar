package com.kush.service;

import com.kush.dto.MaintenanceDTO;
import com.kush.entity.Maintenance;
import com.kush.entity.Vehicle;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.MaintenanceRepository;
import com.kush.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public MaintenanceDTO scheduleMaintenance(Long vehicleId, MaintenanceDTO request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        Maintenance maintenance = Maintenance.builder()
                .maintenanceType(request.getMaintenanceType())
                .maintenanceDate(request.getMaintenanceDate())
                .description(request.getDescription())
                .cost(request.getCost())
                .status(Maintenance.MaintenanceStatus.SCHEDULED)
                .vehicle(vehicle)
                .build();

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        log.info("Maintenance scheduled for vehicle: {}", vehicleId);

        return mapToDTO(savedMaintenance);
    }

    public MaintenanceDTO getMaintenanceById(Long maintenanceId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found"));
        return mapToDTO(maintenance);
    }

    public List<MaintenanceDTO> getAllMaintenance() {
        return maintenanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceDTO> getMaintenanceByVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        return maintenanceRepository.findByVehicle(vehicle).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceDTO updateMaintenanceStatus(Long maintenanceId, Maintenance.MaintenanceStatus status) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found"));

        maintenance.setStatus(status);
        if (status == Maintenance.MaintenanceStatus.COMPLETED) {
            maintenance.setCompletionDate(LocalDate.now());
        }
        maintenance.setUpdatedAt(LocalDateTime.now());

        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        log.info("Maintenance status updated: {} -> {}", maintenanceId, status);

        return mapToDTO(updatedMaintenance);
    }

    private MaintenanceDTO mapToDTO(Maintenance maintenance) {
        return MaintenanceDTO.builder()
                .maintenanceId(maintenance.getMaintenanceId())
                .maintenanceType(maintenance.getMaintenanceType())
                .maintenanceDate(maintenance.getMaintenanceDate())
                .completionDate(maintenance.getCompletionDate())
                .description(maintenance.getDescription())
                .notes(maintenance.getNotes())
                .cost(maintenance.getCost())
                .status(maintenance.getStatus().name())
                .vehicleId(maintenance.getVehicle().getVehicleId())
                .mileageAtService(maintenance.getMileageAtService())
                .nextServiceMileage(maintenance.getNextServiceMileage())
                .createdAt(maintenance.getCreatedAt())
                .build();
    }
}
