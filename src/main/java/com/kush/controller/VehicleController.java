package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.CreateVehicleRequest;
import com.kush.dto.VehicleDTO;
import com.kush.entity.Vehicle;
import com.kush.service.VehicleService;
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
@RequestMapping("/vehicles")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDTO>> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        log.info("Creating vehicle: {}", request.getLicensePlate());
        VehicleDTO vehicleDTO = vehicleService.createVehicle(request);
        
        ApiResponse<VehicleDTO> response = ApiResponse.<VehicleDTO>builder()
                .success(true)
                .message("Vehicle created successfully")
                .data(vehicleDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VehicleDTO>> getVehicleById(@PathVariable Long id) {
        log.info("Fetching vehicle: {}", id);
        VehicleDTO vehicleDTO = vehicleService.getVehicleById(id);
        
        ApiResponse<VehicleDTO> response = ApiResponse.<VehicleDTO>builder()
                .success(true)
                .message("Vehicle retrieved successfully")
                .data(vehicleDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getAllVehicles() {
        log.info("Fetching all vehicles");
        List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
        
        ApiResponse<List<VehicleDTO>> response = ApiResponse.<List<VehicleDTO>>builder()
                .success(true)
                .message("Vehicles retrieved successfully")
                .data(vehicles)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> searchAvailableVehicles(
            @RequestParam(required = false) String category) {
        log.info("Searching available vehicles by category: {}", category);
        
        List<VehicleDTO> vehicles;
        if (category != null && !category.isEmpty()) {
            vehicles = vehicleService.getAvailableVehiclesByCategory(Vehicle.VehicleCategory.valueOf(category));
        } else {
            vehicles = vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);
        }
        
        ApiResponse<List<VehicleDTO>> response = ApiResponse.<List<VehicleDTO>>builder()
                .success(true)
                .message("Available vehicles retrieved successfully")
                .data(vehicles)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Object>> getVehicleCategories() {
        log.info("Fetching vehicle categories");
        
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Vehicle categories retrieved successfully")
                .data(Vehicle.VehicleCategory.values())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getVehiclesByStatus(@PathVariable String status) {
        log.info("Fetching vehicles by status: {}", status);
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.valueOf(status));
        
        ApiResponse<List<VehicleDTO>> response = ApiResponse.<List<VehicleDTO>>builder()
                .success(true)
                .message("Vehicles retrieved successfully")
                .data(vehicles)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDTO>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody CreateVehicleRequest request) {
        log.info("Updating vehicle: {}", id);
        VehicleDTO vehicleDTO = vehicleService.updateVehicle(id, request);
        
        ApiResponse<VehicleDTO> response = ApiResponse.<VehicleDTO>builder()
                .success(true)
                .message("Vehicle updated successfully")
                .data(vehicleDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<VehicleDTO>> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Updating vehicle status: {} -> {}", id, status);
        VehicleDTO vehicleDTO = vehicleService.updateVehicleStatus(id, Vehicle.VehicleStatus.valueOf(status));
        
        ApiResponse<VehicleDTO> response = ApiResponse.<VehicleDTO>builder()
                .success(true)
                .message("Vehicle status updated successfully")
                .data(vehicleDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteVehicle(@PathVariable Long id) {
        log.info("Deleting vehicle: {}", id);
        vehicleService.deleteVehicle(id);
        
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Vehicle deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
