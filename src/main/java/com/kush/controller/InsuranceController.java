package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.entity.Insurance;
import com.kush.service.InsuranceService;
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
@RequestMapping("/insurance")
public class InsuranceController {

    private final InsuranceService insuranceService;

    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Insurance>> createInsurance(@Valid @RequestBody Insurance insurance) {
        log.info("Creating insurance: {}", insurance.getPolicyNumber());
        Insurance createdInsurance = insuranceService.createInsurance(insurance);
        
        ApiResponse<Insurance> response = ApiResponse.<Insurance>builder()
                .success(true)
                .message("Insurance created successfully")
                .data(createdInsurance)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Insurance>> getInsuranceById(@PathVariable Long id) {
        log.info("Fetching insurance: {}", id);
        Insurance insurance = insuranceService.getInsuranceById(id);
        
        ApiResponse<Insurance> response = ApiResponse.<Insurance>builder()
                .success(true)
                .message("Insurance retrieved successfully")
                .data(insurance)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<Insurance>>> getAllInsurance() {
        log.info("Fetching all insurance records");
        List<Insurance> insurances = insuranceService.getAllInsurance();
        
        ApiResponse<List<Insurance>> response = ApiResponse.<List<Insurance>>builder()
                .success(true)
                .message("Insurance records retrieved successfully")
                .data(insurances)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Insurance>>> getInsuranceByVehicle(@PathVariable Long vehicleId) {
        log.info("Fetching insurance for vehicle: {}", vehicleId);
        List<Insurance> insurances = insuranceService.getInsuranceByVehicle(vehicleId);
        
        ApiResponse<List<Insurance>> response = ApiResponse.<List<Insurance>>builder()
                .success(true)
                .message("Vehicle insurance retrieved successfully")
                .data(insurances)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Insurance>>> getActiveInsurance() {
        log.info("Fetching active insurance");
        List<Insurance> insurances = insuranceService.getActiveInsurance();
        
        ApiResponse<List<Insurance>> response = ApiResponse.<List<Insurance>>builder()
                .success(true)
                .message("Active insurance retrieved successfully")
                .data(insurances)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Insurance>> updateInsurance(
            @PathVariable Long id,
            @Valid @RequestBody Insurance insuranceDetails) {
        log.info("Updating insurance: {}", id);
        Insurance updatedInsurance = insuranceService.updateInsurance(id, insuranceDetails);
        
        ApiResponse<Insurance> response = ApiResponse.<Insurance>builder()
                .success(true)
                .message("Insurance updated successfully")
                .data(updatedInsurance)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteInsurance(@PathVariable Long id) {
        log.info("Deleting insurance: {}", id);
        insuranceService.deleteInsurance(id);
        
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Insurance deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
