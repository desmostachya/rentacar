package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.CreateLocationRequest;
import com.kush.dto.LocationDTO;
import com.kush.service.LocationService;
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
@RequestMapping("/locations")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationDTO>> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        log.info("Creating location: {}", request.getLocationName());
        LocationDTO locationDTO = locationService.createLocation(request);
        
        ApiResponse<LocationDTO> response = ApiResponse.<LocationDTO>builder()
                .success(true)
                .message("Location created successfully")
                .data(locationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LocationDTO>> getLocationById(@PathVariable Long id) {
        log.info("Fetching location: {}", id);
        LocationDTO locationDTO = locationService.getLocationById(id);
        
        ApiResponse<LocationDTO> response = ApiResponse.<LocationDTO>builder()
                .success(true)
                .message("Location retrieved successfully")
                .data(locationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAllLocations() {
        log.info("Fetching all locations");
        List<LocationDTO> locations = locationService.getAllLocations();
        
        ApiResponse<List<LocationDTO>> response = ApiResponse.<List<LocationDTO>>builder()
                .success(true)
                .message("Locations retrieved successfully")
                .data(locations)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getActiveLocations() {
        log.info("Fetching active locations");
        List<LocationDTO> locations = locationService.getActiveLocations();
        
        ApiResponse<List<LocationDTO>> response = ApiResponse.<List<LocationDTO>>builder()
                .success(true)
                .message("Active locations retrieved successfully")
                .data(locations)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LocationDTO>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody CreateLocationRequest request) {
        log.info("Updating location: {}", id);
        LocationDTO locationDTO = locationService.updateLocation(id, request);
        
        ApiResponse<LocationDTO> response = ApiResponse.<LocationDTO>builder()
                .success(true)
                .message("Location updated successfully")
                .data(locationDTO)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteLocation(@PathVariable Long id) {
        log.info("Deleting location: {}", id);
        locationService.deleteLocation(id);
        
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Location deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
