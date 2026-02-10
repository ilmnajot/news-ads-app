package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.AdsAssignmentDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;
import uz.ilmnajot.newsadsapp.entity.AdsCreative;
import uz.ilmnajot.newsadsapp.entity.AdsPlacement;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.mapper.AdsAssignmentMapper;
import uz.ilmnajot.newsadsapp.repository.AdsAssignmentRepository;
import uz.ilmnajot.newsadsapp.repository.AdsCampaignRepository;
import uz.ilmnajot.newsadsapp.repository.AdsCreativeRepository;
import uz.ilmnajot.newsadsapp.repository.AdsPlacementRepository;
import uz.ilmnajot.newsadsapp.service.AdsAssignmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsAssignmentServiceImpl implements AdsAssignmentService {

    private final AdsAssignmentRepository assignmentRepository;
    private final AdsPlacementRepository placementRepository;
    private final AdsCampaignRepository campaignRepository;
    private final AdsCreativeRepository creativeRepository;
    private final AdsAssignmentMapper assignmentMapper;
    private final AdsAssignmentRepository adsAssignmentRepository;

    // CREATE Assignment
    @Transactional
    @Override
    public ApiResponse createAssignment(AdsAssignmentDto.CreateAssignment request) {

        // Validate entities
        AdsPlacement placement = placementRepository.findById(request.getPlacementId())
                .orElseThrow(() -> new ResourceNotFoundException("Placement not found"));

        AdsCampaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        AdsCreative creative = creativeRepository.findById(request.getCreativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Creative not found"));

        // Validate creative belongs to campaign
        if (!creative.getCampaign().getId().equals(campaign.getId())) {
            throw new BadRequestException("Creative does not belong to the specified campaign");
        }

        // Validate dates
        if (request.getEndAt() != null && request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("End date must be after start date");
        }

        AdsAssignment assignment = AdsAssignment.builder()
                .placement(placement)
                .campaign(campaign)
                .creative(creative)
                .weight(request.getWeight())
                .langFilter(request.getLangFilter())
                .categoryFilter(request.getCategoryFilter())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .isActive(true)
                .build();

        AdsAssignment saved = assignmentRepository.save(assignment);

        log.info("Assignment created: id={}, placement={}, campaign={}, weight={}",
                saved.getId(), placement.getCode(), campaign.getName(), request.getWeight());

        return ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Assignment created successfully")
                .data(this.assignmentMapper.toDto(saved))
                .build();
    }

    // GET All Assignments
    @Override
    public ApiResponse getAllAssignments() {

        List<AdsAssignment> assignments = assignmentRepository.findAll();

        List<AdsAssignmentDto> dtos = assignments.stream()
                .map(this.assignmentMapper::toDto)
                .collect(Collectors.toList());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dtos)
                .build();
    }

    // GET Assignment by ID
    @Override
    public ApiResponse getAssignmentById(Long id) {

        AdsAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(this.assignmentMapper.toDto(assignment))
                .build();
    }

    // UPDATE Assignment (PUT)
    @Transactional
    @Override
    public ApiResponse updateAssignment(Long id, AdsAssignmentDto.UpdateAssignment request) {

        AdsAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Update fields
        if (request.getWeight() != null) {
            assignment.setWeight(request.getWeight());
        }

        if (request.getLangFilter() != null) {
            assignment.setLangFilter(request.getLangFilter());
        }

        if (request.getCategoryFilter() != null) {
            assignment.setCategoryFilter(request.getCategoryFilter());
        }

        if (request.getStartAt() != null) {
            assignment.setStartAt(request.getStartAt());
        }

        if (request.getEndAt() != null) {
            // Validate
            LocalDateTime startAt = request.getStartAt() != null ? request.getStartAt() : assignment.getStartAt();
            if (request.getEndAt().isBefore(startAt)) {
                throw new BadRequestException("End date must be after start date");
            }
            assignment.setEndAt(request.getEndAt());
        }

        if (request.getIsActive() != null) {
            assignment.setIsActive(request.getIsActive());
        }

        assignment.setUpdatedAt(LocalDateTime.now());

        AdsAssignment updated = assignmentRepository.save(assignment);

        log.info("Assignment updated: id={}", id);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Assignment updated successfully")
                .data(this.assignmentMapper.toDto(updated))
                .build();
    }

    // DELETE Assignment
    @Transactional
    @Override
    public ApiResponse deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment not found");
        }
        assignmentRepository.deleteById(id);
        log.info("Assignment deleted: id={}", id);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Assignment deleted successfully")
                .build();
    }

    @Override
    @Cacheable(value = "publicAds", key = "'placement:' + #placementCode + ':lang:' + (#lang ?: 'uz') + ':cat:' + (#categoryId ?: 'all')", unless = "#result == null")
    // findActiveAssignmentsByPlacement
    public ApiResponse findActiveAssignmentsByPlacement(String placementCode, String lang, Long categoryId) {

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

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .data(this.assignmentMapper.toDto(selected))
                .build();
    }

    // matchesLangFilter
    private boolean matchesLangFilter(AdsAssignment assignment, String lang) {
        if (assignment.getLangFilter() == null || assignment.getLangFilter().isEmpty()) {
            return true; // No filter means all languages
        }
        return assignment.getLangFilter().contains(lang);
    }

    // matchesCategoryFilter
    private boolean matchesCategoryFilter(AdsAssignment assignment, Long categoryId) {
        if (assignment.getCategoryFilter() == null || assignment.getCategoryFilter().isEmpty()) {
            return true; // No filter means all categories
        }
        if (categoryId == null) {
            return true;
        }
        return assignment.getCategoryFilter().contains(categoryId);
    }

    // selectByWeight
    private AdsAssignment selectByWeight(List<AdsAssignment> assignments) {
        Random random = new Random();
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