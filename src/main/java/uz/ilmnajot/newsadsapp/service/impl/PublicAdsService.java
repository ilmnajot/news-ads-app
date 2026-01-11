package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.PublicAdDto;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;
import uz.ilmnajot.newsadsapp.entity.AdsCreativeTranslation;
import uz.ilmnajot.newsadsapp.repository.AdsAssignmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicAdsService {

    private final AdsAssignmentRepository assignmentRepository;
    private final Random random = new Random();

    /**
     * Get ad for placement (with cache)
     * Taskda: GET /public/ads/{placementCode}?lang=uz&categoryId=
     */
    @Cacheable(
            value = "publicAds",
            key = "'placement:' + #placementCode + ':lang:' + #lang + ':cat:' + (#categoryId != null ? #categoryId : 'all')",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public PublicAdDto getAdForPlacement(String placementCode, String lang, Long categoryId) {

        log.info("PUBLIC AD - Cache MISS: placement={}, lang={}, categoryId={}",
                placementCode, lang, categoryId);

        LocalDateTime now = LocalDateTime.now();

        // Get all active assignments for placement
        List<AdsAssignment> assignments = assignmentRepository
                .findActiveAssignmentsForPlacement(placementCode, now);

        if (assignments.isEmpty()) {
            log.warn("No active ads found for placement: {}", placementCode);
            return null;
        }

        // Filter by lang and category
        List<AdsAssignment> filtered = assignments.stream()
                .filter(a -> matchesLangFilter(a, lang))
                .filter(a -> matchesCategoryFilter(a, categoryId))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            log.warn("No ads match filters: placement={}, lang={}, categoryId={}",
                    placementCode, lang, categoryId);
            return null;
        }

        // Select ad based on weight (weighted random selection)
        AdsAssignment selected = selectByWeight(filtered);

        log.info("Ad selected: assignmentId={}, campaignId={}, weight={}",
                selected.getId(), selected.getCampaign().getId(), selected.getWeight());

        return mapToPublicDto(selected, lang);
    }

    /**
     * Check if assignment matches lang filter
     */
    private boolean matchesLangFilter(AdsAssignment assignment, String lang) {
        List<String> langFilter = assignment.getLangFilter();

        // No filter = match all
        if (langFilter == null || langFilter.isEmpty()) {
            return true;
        }

        // Check if lang is in filter
        return langFilter.contains(lang);
    }

    /**
     * Check if assignment matches category filter
     */
    private boolean matchesCategoryFilter(AdsAssignment assignment, Long categoryId) {
        List<Long> categoryFilter = assignment.getCategoryFilter();

        // No filter = match all
        if (categoryFilter == null || categoryFilter.isEmpty()) {
            return true;
        }

        // No categoryId provided = don't match
        if (categoryId == null) {
            return false;
        }

        // Check if categoryId is in filter
        return categoryFilter.contains(categoryId);
    }

    /**
     * Weighted random selection
     * <p>
     * Example:
     * - Ad A: weight 70
     * - Ad B: weight 30
     * Total: 100
     * <p>
     * Random number 0-99:
     * - 0-69 → Ad A (70%)
     * - 70-99 → Ad B (30%)
     */
    private AdsAssignment selectByWeight(List<AdsAssignment> assignments) {

        // Calculate total weight
        int totalWeight = assignments.stream()
                .mapToInt(AdsAssignment::getWeight)
                .sum();

        if (totalWeight == 0) {
            // All weights are 0, return random
            return assignments.get(random.nextInt(assignments.size()));
        }

        // Generate random number [0, totalWeight)
        int randomValue = random.nextInt(totalWeight);

        // Find assignment based on weight ranges
        int currentWeight = 0;
        for (AdsAssignment assignment : assignments) {
            currentWeight += assignment.getWeight();
            if (randomValue < currentWeight) {
                return assignment;
            }
        }

        // Fallback (should never reach here)
        return assignments.get(assignments.size() - 1);
    }

    /**
     * Entity → Public DTO mapping
     */
    private PublicAdDto mapToPublicDto(AdsAssignment assignment, String lang) {

        // Get translation for requested language
        AdsCreativeTranslation translation = assignment.getCreative().getTranslations().stream()
                .filter(tr -> tr.getLang().equals(lang))
                .findFirst()
                .orElse(null);

        return PublicAdDto.builder()
                .id(assignment.getId())
                .type(assignment.getCreative().getType().name())
                .title(translation != null ? translation.getTitle() : null)
                .altText(translation != null ? translation.getAltText() : null)
                .imageUrl(assignment.getCreative().getImageMedia() != null
                        ? assignment.getCreative().getImageMedia().getUrl()
                        : null)
                .htmlSnippet(assignment.getCreative().getHtmlSnippet())
                .landingUrl(assignment.getCreative().getLandingUrl())
                .build();
    }
}