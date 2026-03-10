package com.kush.service;

import com.kush.dto.CreateLocationRequest;
import com.kush.dto.LocationDTO;
import com.kush.entity.Location;
import com.kush.exception.DuplicateResourceException;
import com.kush.exception.ResourceNotFoundException;
import com.kush.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationDTO createLocation(CreateLocationRequest request) {
        if (locationRepository.findByLocationName(request.getLocationName()).isPresent()) {
            throw new DuplicateResourceException("Location name already exists");
        }

        Location location = Location.builder()
                .locationName(request.getLocationName())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .phoneNumber(request.getPhoneNumber())
                .status(Location.LocationStatus.ACTIVE)
                .build();

        Location savedLocation = locationRepository.save(location);
        log.info("Location created: {}", savedLocation.getLocationName());

        return mapToDTO(savedLocation);
    }

    public LocationDTO getLocationById(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));
        return mapToDTO(location);
    }

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<LocationDTO> getActiveLocations() {
        return locationRepository.findByStatus(Location.LocationStatus.ACTIVE).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LocationDTO updateLocation(Long locationId, CreateLocationRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));

        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setPostalCode(request.getPostalCode());
        location.setPhoneNumber(request.getPhoneNumber());
        location.setUpdatedAt(LocalDateTime.now());

        Location updatedLocation = locationRepository.save(location);
        log.info("Location updated: {}", updatedLocation.getLocationName());

        return mapToDTO(updatedLocation);
    }

    public void deleteLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));

        locationRepository.delete(location);
        log.info("Location deleted: {}", location.getLocationName());
    }

    private LocationDTO mapToDTO(Location location) {
        return LocationDTO.builder()
                .locationId(location.getLocationId())
                .locationName(location.getLocationName())
                .address(location.getAddress())
                .city(location.getCity())
                .postalCode(location.getPostalCode())
                .phoneNumber(location.getPhoneNumber())
                .status(location.getStatus().name())
                .createdAt(location.getCreatedAt())
                .build();
    }
}
