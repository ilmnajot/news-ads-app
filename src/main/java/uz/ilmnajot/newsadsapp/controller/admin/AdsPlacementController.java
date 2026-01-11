package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.AdsPlacementDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.service.AdsPlacementService;

@RestController
@RequestMapping("/api/v1/admin/ads/placements")
@RequiredArgsConstructor
public class AdsPlacementController {

    private final AdsPlacementService placementService;

    /**
     * CREATE Placement
     * POST /admin/ads/placements
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> createPlacement(
            @Valid @RequestBody AdsPlacementDto.CreatePlacement request) {

        ApiResponse response = placementService.createPlacement(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * GET All Placements
     * GET /admin/ads/placements
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAllPlacements() {

        ApiResponse response = placementService.getAllPlacements();
        return ResponseEntity.ok(response);
    }

    /**
     * GET Placement by ID
     * GET /admin/ads/placements/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getPlacementById(@PathVariable Long id) {

        ApiResponse response = placementService.getPlacementById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE Placement (PATCH)
     * PATCH /admin/ads/placements/{id}
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updatePlacement(
            @PathVariable Long id,
            @Valid @RequestBody AdsPlacementDto.UpdatePlacement request) {

        ApiResponse response = placementService.updatePlacement(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE Placement
     * DELETE /admin/ads/placements/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePlacement(@PathVariable Long id) {

        ApiResponse response = placementService.deletePlacement(id);
        return ResponseEntity.ok(response);
    }
}