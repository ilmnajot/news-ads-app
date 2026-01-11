package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.AdsCampaignDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.service.AdsCampaignService;

@RestController
@RequestMapping("/api/v1/admin/ads/campaigns")
@RequiredArgsConstructor
public class AdsCampaignController {

    private final AdsCampaignService campaignService;

    /**
     * CREATE Campaign
     * POST /admin/ads/campaigns
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> createCampaign(
            @Valid @RequestBody AdsCampaignDto.CreateCampaign request) {
        
        ApiResponse response = campaignService.createCampaign(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * GET All Campaigns
     * GET /admin/ads/campaigns
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAllCampaigns() {
        
        ApiResponse response = campaignService.getAllCampaigns();
        return ResponseEntity.ok(response);
    }

    /**
     * GET Campaign by ID
     * GET /admin/ads/campaigns/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getCampaignById(@PathVariable Long id) {
        
        ApiResponse response = campaignService.getCampaignById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE Campaign (PUT - full update)
     * PUT /admin/ads/campaigns/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody AdsCampaignDto.UpdateCampaign request) {
        
        ApiResponse response = campaignService.updateCampaign(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE Campaign Status (PATCH)
     * PATCH /admin/ads/campaigns/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updateCampaignStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdsCampaignDto.UpdateCampaignStatus request) {
        
        ApiResponse response = campaignService.updateCampaignStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE Campaign
     * DELETE /admin/ads/campaigns/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCampaign(@PathVariable Long id) {
        
        ApiResponse response = campaignService.deleteCampaign(id);
        return ResponseEntity.ok(response);
    }
}