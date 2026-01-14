package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse createPlacement(
            @Valid @RequestBody AdsPlacementDto.CreatePlacement request) {
        return placementService.createPlacement(request);
    }

    /**
     * GET All Placements
     * GET /admin/ads/placements
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getAllPlacements() {
        return placementService.getAllPlacements();
    }

    /**
     * GET Placement by ID
     * GET /admin/ads/placements/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getPlacementById(@PathVariable Long id) {
        return placementService.getPlacementById(id);
    }

    /**
     * UPDATE Placement (PATCH)
     * PATCH /admin/ads/placements/{id}
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updatePlacement(
            @PathVariable Long id,
            @Valid @RequestBody AdsPlacementDto.UpdatePlacement request) {
        return placementService.updatePlacement(id, request);
    }

    /**
     * DELETE Placement
     * DELETE /admin/ads/placements/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse deletePlacement(@PathVariable Long id) {
        return placementService.deletePlacement(id);
    }
}