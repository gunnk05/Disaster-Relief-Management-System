package com.relieflink.resource;

import com.relieflink.common.exception.BadRequestException;
import com.relieflink.common.exception.ResourceNotFoundException;
import com.relieflink.resource.dto.ResourceRequest;
import com.relieflink.resource.dto.ResourceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final List<String> VALID_TYPES =
            Arrays.asList("food", "water", "medical", "clothing", "other");

    private final ResourceRepository resourceRepository;
    private final LocationRepository locationRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               LocationRepository locationRepository) {
        this.resourceRepository = resourceRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAllWithLocation().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        validateType(request.getType());

        Resource resource = Resource.builder()
                .name(request.getName().trim())
                .type(request.getType())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .unit(request.getUnit())
                .location(request.getLocationId() != null
                        ? locationRepository.findById(request.getLocationId())
                            .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()))
                        : null)
                .build();

        resource = resourceRepository.save(resource);
        return toResponse(resource);
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        validateType(request.getType());

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));

        resource.setName(request.getName().trim());
        resource.setType(request.getType());
        resource.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        resource.setUnit(request.getUnit());
        resource.setLocation(request.getLocationId() != null
                ? locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()))
                : null);

        resource = resourceRepository.save(resource);
        return toResponse(resource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource", id);
        }
        resourceRepository.deleteById(id);
    }

    private void validateType(String type) {
        if (!VALID_TYPES.contains(type)) {
            throw new BadRequestException("Invalid resource type: " + type);
        }
    }

    private ResourceResponse toResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getType())
                .quantity(resource.getQuantity())
                .unit(resource.getUnit())
                .locationId(resource.getLocation() != null ? resource.getLocation().getId() : null)
                .locationName(resource.getLocation() != null ? resource.getLocation().getName() : null)
                .lastUpdated(resource.getLastUpdated())
                .build();
    }
}
