package uz.ilmnajot.newsadsapp.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.entity.AdsPlacement;
import uz.ilmnajot.newsadsapp.repository.AdsPlacementRepository;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/ads/placements")
@RequiredArgsConstructor
public class AdsPlacementController {

    private final AdsPlacementRepository adsPlacementRepository;

    @GetMapping
    public ResponseEntity<Page<AdsPlacement>> getAllPlacements(Pageable pageable) {
        return ResponseEntity.ok(adsPlacementRepository.findAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<AdsPlacement> createPlacement(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Placement code is required");
        }
        
        if (adsPlacementRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Placement with code already exists: " + code);
        }
        
        AdsPlacement placement = AdsPlacement.builder()
                .code(code)
                .title(request.get("title"))
                .description(request.get("description"))
                .isActive(true)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(adsPlacementRepository.save(placement));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdsPlacement> getPlacementById(@PathVariable Long id) {
        AdsPlacement placement = adsPlacementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement not found"));
        return ResponseEntity.ok(placement);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<AdsPlacement> updatePlacement(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> updates) {
        AdsPlacement placement = adsPlacementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement not found"));
        
        if (updates.containsKey("title")) {
            placement.setTitle(updates.get("title").toString());
        }
        if (updates.containsKey("description")) {
            placement.setDescription(updates.get("description").toString());
        }
        if (updates.containsKey("isActive")) {
            placement.setIsActive(Boolean.parseBoolean(updates.get("isActive").toString()));
        }
        
        return ResponseEntity.ok(adsPlacementRepository.save(placement));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlacement(@PathVariable Long id) {
        if (!adsPlacementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Placement not found");
        }
        adsPlacementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

