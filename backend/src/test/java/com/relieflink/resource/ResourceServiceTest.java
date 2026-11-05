package com.relieflink.resource;

import com.relieflink.common.exception.BadRequestException;
import com.relieflink.common.exception.ResourceNotFoundException;
import com.relieflink.resource.dto.ResourceRequest;
import com.relieflink.resource.dto.ResourceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Resource testResource;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
                .id(1L)
                .name("Chennai")
                .build();

        testResource = Resource.builder()
                .id(1L)
                .name("Rice Bags")
                .type("food")
                .quantity(500)
                .unit("kg")
                .location(testLocation)
                .build();
    }

    @Test
    @DisplayName("getAllResources() should return mapped DTOs")
    void getAllResources_ReturnsList() {
        when(resourceRepository.findAllWithLocation()).thenReturn(List.of(testResource));

        List<ResourceResponse> result = resourceService.getAllResources();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rice Bags");
        assertThat(result.get(0).getLocationName()).isEqualTo("Chennai");
    }

    @Test
    @DisplayName("createResource() should persist and return new resource")
    void createResource_ValidRequest_ReturnsResponse() {
        ResourceRequest request = new ResourceRequest();
        request.setName("Water Bottles");
        request.setType("water");
        request.setQuantity(1000);
        request.setUnit("liters");
        request.setLocationId(1L);

        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(resourceRepository.save(any(Resource.class))).thenAnswer(inv -> {
            Resource r = inv.getArgument(0);
            r.setId(2L);
            return r;
        });

        ResourceResponse response = resourceService.createResource(request);

        assertThat(response.getName()).isEqualTo("Water Bottles");
        assertThat(response.getType()).isEqualTo("water");
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    @DisplayName("createResource() should throw BadRequestException for invalid type")
    void createResource_InvalidType_ThrowsBadRequest() {
        ResourceRequest request = new ResourceRequest();
        request.setName("Electronics");
        request.setType("electronics");
        request.setQuantity(10);

        assertThatThrownBy(() -> resourceService.createResource(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid resource type");
    }

    @Test
    @DisplayName("updateResource() should throw ResourceNotFoundException for missing ID")
    void updateResource_NotFound_ThrowsException() {
        ResourceRequest request = new ResourceRequest();
        request.setName("Rice");
        request.setType("food");

        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.updateResource(999L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteResource() should call repository when ID exists")
    void deleteResource_Exists_Deletes() {
        when(resourceRepository.existsById(1L)).thenReturn(true);

        resourceService.deleteResource(1L);

        verify(resourceRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteResource() should throw ResourceNotFoundException for missing ID")
    void deleteResource_NotFound_ThrowsException() {
        when(resourceRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> resourceService.deleteResource(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
