package com.kush.service;

import com.kush.dto.CreateVehicleRequest;
import com.kush.dto.VehicleDTO;
import com.kush.entity.Location;
import com.kush.entity.Vehicle;
import com.kush.exception.DuplicateResourceException;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.LocationRepository;
import com.kush.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;

    public VehicleService(VehicleRepository vehicleRepository, LocationRepository locationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.locationRepository = locationRepository;
    }

    public VehicleDTO createVehicle(CreateVehicleRequest request) {
        if (vehicleRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
            throw new DuplicateResourceException("License plate already exists");
        }

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .category(Vehicle.VehicleCategory.valueOf(request.getCategory()))
                .color(request.getColor())
                .mileage(request.getMileage())
                .dailyRate(request.getDailyRate())
                .fuelCapacity(request.getFuelCapacity())
                .fuelType(Vehicle.FuelType.valueOf(request.getFuelType()))
                .passengerCapacity(request.getPassengerCapacity())
                .hasAirConditioning(request.getHasAirConditioning())
                .hasAutomaticTransmission(request.getHasAutomaticTransmission())
                .description(request.getDescription())
                .location(location)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {} - {}", savedVehicle.getLicensePlate(), savedVehicle.getModel());

        return mapToDTO(savedVehicle);
    }

    public VehicleDTO getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
        return mapToDTO(vehicle);
    }

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleDTO> getVehiclesByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleDTO> getVehiclesByCategory(Vehicle.VehicleCategory category) {
        return vehicleRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleDTO> getAvailableVehiclesByCategory(Vehicle.VehicleCategory category) {
        return vehicleRepository.findAvailableByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO updateVehicle(Long vehicleId, CreateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setColor(request.getColor());
        vehicle.setMileage(request.getMileage());
        vehicle.setDailyRate(request.getDailyRate());
        vehicle.setDescription(request.getDescription());
        vehicle.setLocation(location);
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated: {}", updatedVehicle.getLicensePlate());

        return mapToDTO(updatedVehicle);
    }

    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        vehicleRepository.delete(vehicle);
        log.info("Vehicle deleted: {}", vehicle.getLicensePlate());
    }

    public VehicleDTO updateVehicleStatus(Long vehicleId, Vehicle.VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        vehicle.setStatus(status);
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle status updated: {} -> {}", updatedVehicle.getLicensePlate(), status);

        return mapToDTO(updatedVehicle);
    }

    private VehicleDTO mapToDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .vehicleId(vehicle.getVehicleId())
                .licensePlate(vehicle.getLicensePlate())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .category(vehicle.getCategory().name())
                .color(vehicle.getColor())
                .mileage(vehicle.getMileage())
                .dailyRate(vehicle.getDailyRate())
                .status(vehicle.getStatus().name())
                .fuelType(vehicle.getFuelType().name())
                .passengerCapacity(vehicle.getPassengerCapacity())
                .hasAirConditioning(vehicle.getHasAirConditioning())
                .hasAutomaticTransmission(vehicle.getHasAutomaticTransmission())
                .description(vehicle.getDescription())
                .locationName(vehicle.getLocation().getLocationName())
                .createdAt(vehicle.getCreatedAt())
                .build();
    }
}
