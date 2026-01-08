package uz.ilmnajot.newsadsapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;
import uz.ilmnajot.newsadsapp.entity.AdsCreative;
import uz.ilmnajot.newsadsapp.repository.AdsAssignmentRepository;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public/ads")
@RequiredArgsConstructor
public class PublicAdsController {

    private final AdsAssignmentRepository adsAssignmentRepository;
    private final Random random = new Random();

    @GetMapping("/{placementCode}")
    public ResponseEntity<AdsCreative> getAd(@PathVariable String placementCode,
                                             @RequestParam(defaultValue = "uz") String lang,
                                             @RequestParam(required = false) Long categoryId) {
        LocalDateTime now = LocalDateTime.now();
        List<AdsAssignment> assignments = adsAssignmentRepository.findActiveAssignmentsByPlacement(placementCode, now);
        
        // Filter by language and category
        List<AdsAssignment> filtered = assignments.stream()
                .filter(a -> matchesLangFilter(a, lang))
                .filter(a -> matchesCategoryFilter(a, categoryId))
                .collect(Collectors.toList());
        
        if (filtered.isEmpty()) {
            throw new ResourceNotFoundException("No active ad found for placement: " + placementCode);
        }
        
        // Weight-based selection
        AdsAssignment selected = selectByWeight(filtered);
        
        return ResponseEntity.ok(selected.getCreative());
    }

    private boolean matchesLangFilter(AdsAssignment assignment, String lang) {
        if (assignment.getLangFilter() == null || assignment.getLangFilter().isEmpty()) {
            return true; // No filter means all languages
        }
        return assignment.getLangFilter().contains(lang);
    }

    private boolean matchesCategoryFilter(AdsAssignment assignment, Long categoryId) {
        if (assignment.getCategoryFilter() == null || assignment.getCategoryFilter().isEmpty()) {
            return true; // No filter means all categories
        }
        if (categoryId == null) {
            return true;
        }
        return assignment.getCategoryFilter().contains(categoryId);
    }

    private AdsAssignment selectByWeight(List<AdsAssignment> assignments) {
        int totalWeight = assignments.stream()
                .mapToInt(a -> a.getWeight() != null ? a.getWeight() : 100)
                .sum();
        
        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (AdsAssignment assignment : assignments) {
            currentWeight += assignment.getWeight() != null ? assignment.getWeight() : 100;
            if (randomValue < currentWeight) {
                return assignment;
            }
        }
        
        return assignments.get(0); // Fallback
    }
}
