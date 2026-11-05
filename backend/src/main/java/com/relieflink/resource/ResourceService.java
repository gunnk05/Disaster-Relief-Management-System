package com.relieflink.resource;

import com.relieflink.resource.dto.ResourceRequest;
import com.relieflink.resource.dto.ResourceResponse;

import java.util.List;

/**
 * Service interface for relief resource operations.
 */
public interface ResourceService {

    List<ResourceResponse> getAllResources();

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);
}
