package com.relieflink.resource;

import com.relieflink.resource.dto.LocationRequest;
import com.relieflink.resource.dto.LocationResponse;

import java.util.List;

public interface LocationService {

    List<LocationResponse> getAllLocations();

    LocationResponse createLocation(LocationRequest request);

    LocationResponse updateLocation(Long id, LocationRequest request);

    void deleteLocation(Long id);
}
