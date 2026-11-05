package com.relieflink.resource;

import com.relieflink.common.enums.WeatherAlert;
import com.relieflink.common.exception.BadRequestException;
import com.relieflink.common.exception.ResourceNotFoundException;
import com.relieflink.resource.dto.LocationRequest;
import com.relieflink.resource.dto.LocationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LocationResponse createLocation(LocationRequest request) {
        WeatherAlert alert = parseWeatherAlert(request.getWeatherAlert());

        Location location = Location.builder()
                .name(request.getName().trim())
                .region(request.getRegion() != null ? request.getRegion().trim() : null)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .weatherCondition(request.getWeatherCondition() != null
                        ? request.getWeatherCondition().trim() : null)
                .weatherAlert(alert)
                .build();

        location = locationRepository.save(location);
        return toResponse(location);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(Long id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        WeatherAlert alert = parseWeatherAlert(request.getWeatherAlert());

        location.setName(request.getName().trim());
        location.setRegion(request.getRegion() != null ? request.getRegion().trim() : null);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setWeatherCondition(request.getWeatherCondition() != null
                ? request.getWeatherCondition().trim() : null);
        location.setWeatherAlert(alert);

        location = locationRepository.save(location);
        return toResponse(location);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location", id);
        }
        locationRepository.deleteById(id);
    }

    private WeatherAlert parseWeatherAlert(String alert) {
        if (alert == null || alert.isBlank()) return WeatherAlert.none;
        try {
            return WeatherAlert.valueOf(alert.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid weather alert level: " + alert);
        }
    }

    private LocationResponse toResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .region(location.getRegion())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .weatherCondition(location.getWeatherCondition())
                .weatherAlert(location.getWeatherAlert() != null
                        ? location.getWeatherAlert().name() : "none")
                .lastWeatherUpdate(location.getLastWeatherUpdate())
                .build();
    }
}
