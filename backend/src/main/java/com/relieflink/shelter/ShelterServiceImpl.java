package com.relieflink.shelter;

import com.relieflink.common.exception.BadRequestException;
import com.relieflink.common.exception.ResourceNotFoundException;
import com.relieflink.resource.Location;
import com.relieflink.resource.LocationRepository;
import com.relieflink.shelter.dto.ShelterRequest;
import com.relieflink.shelter.dto.ShelterResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShelterServiceImpl implements ShelterService {

    private final ShelterRepository shelterRepository;
    private final LocationRepository locationRepository;

    public ShelterServiceImpl(ShelterRepository shelterRepository,
                              LocationRepository locationRepository) {
        this.shelterRepository = shelterRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShelterResponse> getAllShelters() {
        return shelterRepository.findAllWithLocation().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShelterResponse createShelter(ShelterRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));

        Shelter shelter = Shelter.builder()
                .name(request.getName().trim())
                .location(location)
                .capacity(request.getCapacity() != null ? request.getCapacity() : 0)
                .currentOccupancy(request.getCurrentOccupancy() != null ? request.getCurrentOccupancy() : 0)
                .contactNumber(request.getContactNumber())
                .build();

        shelter = shelterRepository.save(shelter);
        return toResponse(shelter);
    }

    @Override
    @Transactional
    public ShelterResponse updateShelter(Long id, ShelterRequest request) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter", id));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));

        int capacity = request.getCapacity() != null ? request.getCapacity() : 0;
        int occupancy = request.getCurrentOccupancy() != null ? request.getCurrentOccupancy() : 0;

        if (occupancy > capacity) {
            throw new BadRequestException("Occupancy cannot exceed capacity.");
        }

        shelter.setName(request.getName().trim());
        shelter.setLocation(location);
        shelter.setCapacity(capacity);
        shelter.setCurrentOccupancy(occupancy);
        shelter.setContactNumber(request.getContactNumber());

        shelter = shelterRepository.save(shelter);
        return toResponse(shelter);
    }

    @Override
    @Transactional
    public void deleteShelter(Long id) {
        if (!shelterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shelter", id);
        }
        shelterRepository.deleteById(id);
    }

    private ShelterResponse toResponse(Shelter shelter) {
        return ShelterResponse.builder()
                .id(shelter.getId())
                .name(shelter.getName())
                .locationId(shelter.getLocation() != null ? shelter.getLocation().getId() : null)
                .location(shelter.getLocation() != null ? shelter.getLocation().getName() : null)
                .capacity(shelter.getCapacity())
                .currentOccupancy(shelter.getCurrentOccupancy())
                .contactNumber(shelter.getContactNumber())
                .build();
    }
}
