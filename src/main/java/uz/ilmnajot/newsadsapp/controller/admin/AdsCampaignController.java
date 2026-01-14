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
    public ApiResponse createCampaign(@RequestBody AdsCampaignDto.CreateCampaign request) {
        return campaignService.createCampaign(request);
    }

    /**
     * GET All Campaigns
     * GET /admin/ads/campaigns
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getAllCampaigns() {
        return campaignService.getAllCampaigns();
    }

    /**
     * GET Campaign by ID
     * GET /admin/ads/campaigns/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id);
    }

    /**
     * UPDATE Campaign (PUT - full update)
     * PUT /admin/ads/campaigns/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody AdsCampaignDto.UpdateCampaign request) {
        
        return campaignService.updateCampaign(id, request);
    }

    /**
     * UPDATE Campaign Status (PATCH)
     * PATCH /admin/ads/campaigns/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updateCampaignStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdsCampaignDto.UpdateCampaignStatus request) {
        return campaignService.updateCampaignStatus(id, request);
    }

    /**
     * DELETE Campaign
     * DELETE /admin/ads/campaigns/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse deleteCampaign(@PathVariable Long id) {
        return campaignService.deleteCampaign(id);
    }
}