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
    public ResponseEntity<ApiResponse> createCreative(
            @Valid @RequestBody AdsCreativeDto.CreateCreative request) {
        
        ApiResponse response = creativeService.createCreative(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * GET All Creatives
     * GET /admin/ads/creatives
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAllCreatives() {
        
        ApiResponse response = creativeService.getAllCreatives();
        return ResponseEntity.ok(response);
    }

    /**
     * GET Creative by ID
     * GET /admin/ads/creatives/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getCreativeById(@PathVariable Long id) {
        
        ApiResponse response = creativeService.getCreativeById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE Creative (PUT)
     * PUT /admin/ads/creatives/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updateCreative(
            @PathVariable Long id,
            @Valid @RequestBody AdsCreativeDto.UpdateCreative request) {
        
        ApiResponse response = creativeService.updateCreative(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE Creative
     * DELETE /admin/ads/creatives/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCreative(@PathVariable Long id) {
        
        ApiResponse response = creativeService.deleteCreative(id);
        return ResponseEntity.ok(response);
    }
}