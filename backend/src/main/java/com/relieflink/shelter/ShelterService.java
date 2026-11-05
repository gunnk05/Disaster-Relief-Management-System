package com.relieflink.shelter;

import com.relieflink.shelter.dto.ShelterRequest;
import com.relieflink.shelter.dto.ShelterResponse;

import java.util.List;

public interface ShelterService {

    List<ShelterResponse> getAllShelters();

    ShelterResponse createShelter(ShelterRequest request);

    ShelterResponse updateShelter(Long id, ShelterRequest request);

    void deleteShelter(Long id);
}
