package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.MaintenanceDTO;
import com.kush.entity.Maintenance;
import com.kush.service.MaintenanceService;
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
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/vehicles/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> scheduleMaintenance(
            @PathVariable Long vehicleId,
            @Valid @RequestBody MaintenanceDTO request) {
        log.info("Scheduling maintenance for vehicle: {}", vehicleId);
        MaintenanceDTO maintenanceDTO = maintenanceService.scheduleMaintenance(vehicleId, request);
        
        ApiResponse<MaintenanceDTO> response = ApiResponse.<MaintenanceDTO>builder()
                .success(true)
                .message("Maintenance scheduled successfully")
                .data(maintenanceDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> getMaintenanceById(@PathVariable Long id) {
        log.info("Fetching maintenance record: {}", id);
        MaintenanceDTO maintenanceDTO = maintenanceService.getMaintenanceById(id);
        
        ApiResponse<MaintenanceDTO> response = ApiResponse.<MaintenanceDTO>builder()
                .success(true)
                .message("Maintenance record retrieved successfully")
                .data(maintenanceDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<MaintenanceDTO>>> getAllMaintenance() {
        log.info("Fetching all maintenance records");
        List<MaintenanceDTO> maintenance = maintenanceService.getAllMaintenance();
        
        ApiResponse<List<MaintenanceDTO>> response = ApiResponse.<List<MaintenanceDTO>>builder()
                .success(true)
                .message("Maintenance records retrieved successfully")
                .data(maintenance)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<MaintenanceDTO>>> getMaintenanceByVehicle(@PathVariable Long vehicleId) {
        log.info("Fetching maintenance records for vehicle: {}", vehicleId);
        List<MaintenanceDTO> maintenance = maintenanceService.getMaintenanceByVehicle(vehicleId);
        
        ApiResponse<List<MaintenanceDTO>> response = ApiResponse.<List<MaintenanceDTO>>builder()
                .success(true)
                .message("Maintenance records retrieved successfully")
                .data(maintenance)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> updateMaintenanceStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Updating maintenance status: {} -> {}", id, status);
        MaintenanceDTO maintenanceDTO = maintenanceService.updateMaintenanceStatus(id, Maintenance.MaintenanceStatus.valueOf(status));
        
        ApiResponse<MaintenanceDTO> response = ApiResponse.<MaintenanceDTO>builder()
                .success(true)
                .message("Maintenance status updated successfully")
                .data(maintenanceDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
