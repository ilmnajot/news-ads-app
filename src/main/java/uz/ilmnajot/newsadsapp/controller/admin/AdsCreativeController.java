package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.AdsCreativeDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.service.AdsCreativeService;

@RestController
@RequestMapping("/api/v1/admin/ads/creatives")
@RequiredArgsConstructor
public class AdsCreativeController {

    private final AdsCreativeService creativeService;

    /**
     * CREATE Creative
     * POST /admin/ads/creatives
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse createCreative(
            @Valid @RequestBody AdsCreativeDto.CreateCreative request) {
        return creativeService.createCreative(request);
    }

    /**
     * GET All Creatives
     * GET /admin/ads/creatives
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getAllCreatives() {
        return creativeService.getAllCreatives();
    }

    /**
     * GET Creative by ID
     * GET /admin/ads/creatives/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getCreativeById(@PathVariable Long id) {
        return creativeService.getCreativeById(id);
    }

    /**
     * UPDATE Creative (PUT)
     * PUT /admin/ads/creatives/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updateCreative(
            @PathVariable Long id,
            @Valid @RequestBody AdsCreativeDto.UpdateCreative request) {
        return creativeService.updateCreative(id, request);
    }

    /**
     * DELETE Creative
     * DELETE /admin/ads/creatives/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse deleteCreative(@PathVariable Long id) {
        return creativeService.deleteCreative(id);
    }
}