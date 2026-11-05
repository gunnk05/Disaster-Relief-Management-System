package com.relieflink.shelter;

import com.relieflink.common.ApiResponse;
import com.relieflink.shelter.dto.ShelterRequest;
import com.relieflink.shelter.dto.ShelterResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelters")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public ResponseEntity<List<ShelterResponse>> getAll() {
        return ResponseEntity.ok(shelterService.getAllShelters());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShelterResponse>> create(
            @Valid @RequestBody ShelterRequest request) {
        ShelterResponse response = shelterService.createShelter(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shelter created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long id,
            @Valid @RequestBody ShelterRequest request) {
        shelterService.updateShelter(id, request);
        return ResponseEntity.ok(ApiResponse.success("Shelter updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        shelterService.deleteShelter(id);
        return ResponseEntity.ok(ApiResponse.success("Shelter deleted successfully"));
    }
}
