package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.AdsAssignmentDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;
import uz.ilmnajot.newsadsapp.entity.AdsCreative;
import uz.ilmnajot.newsadsapp.entity.AdsPlacement;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.AdsCampaignRepository;
import uz.ilmnajot.newsadsapp.repository.AdsCreativeRepository;
import uz.ilmnajot.newsadsapp.repository.AdsPlacementRepository;
import uz.ilmnajot.newsadsapp.repository.AdsAssignmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsAssignmentService {

    private final AdsAssignmentRepository assignmentRepository;
    private final AdsPlacementRepository placementRepository;
    private final AdsCampaignRepository campaignRepository;
    private final AdsCreativeRepository creativeRepository;

    /**
     * CREATE Assignment
     */
    @Transactional
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
                .data(mapToDto(saved))
                .build();
    }

    /**
     * GET All Assignments
     */
    @Transactional(readOnly = true)
    public ApiResponse getAllAssignments() {
        
        List<AdsAssignment> assignments = assignmentRepository.findAll();
        
        List<AdsAssignmentDto> dtos = assignments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dtos)
                .build();
    }

    /**
     * GET Assignment by ID
     */
    @Transactional(readOnly = true)
    public ApiResponse getAssignmentById(Long id) {
        
        AdsAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(mapToDto(assignment))
                .build();
    }

    /**
     * UPDATE Assignment (PUT)
     */
    @Transactional
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
                .data(mapToDto(updated))
                .build();
    }

    /**
     * DELETE Assignment
     */
    @Transactional
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

    /**
     * Entity → DTO mapping
     */
    private AdsAssignmentDto mapToDto(AdsAssignment assignment) {
        return AdsAssignmentDto.builder()
                .id(assignment.getId())
                .placementId(assignment.getPlacement().getId())
                .placementCode(assignment.getPlacement().getCode())
                .placementTitle(assignment.getPlacement().getTitle())
                .campaignId(assignment.getCampaign().getId())
                .campaignName(assignment.getCampaign().getName())
                .creativeId(assignment.getCreative().getId())
                .weight(assignment.getWeight())
                .langFilter(assignment.getLangFilter())
                .categoryFilter(assignment.getCategoryFilter())
                .startAt(assignment.getStartAt())
                .endAt(assignment.getEndAt())
                .isActive(assignment.getIsActive())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}