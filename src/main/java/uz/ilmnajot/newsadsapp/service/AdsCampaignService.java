package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.AdsCampaignDto;
import uz.ilmnajot.newsadsapp.dto.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;
import uz.ilmnajot.newsadsapp.enums.CampaignStatus;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.AdsCampaignRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsCampaignService {

    private final AdsCampaignRepository campaignRepository;

    /**
     * CREATE Campaign
     */
    @Transactional
    public ApiResponse createCampaign(AdsCampaignDto.CreateCampaign request) {
        
        // Validate dates
        if (request.getEndAt() != null && request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        AdsCampaign campaign = AdsCampaign.builder()
                .name(request.getName())
                .advertiser(request.getAdvertiser())
                .status(CampaignStatus.DRAFT)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .dailyCapImpressions(request.getDailyCapImpressions())
                .dailyCapClicks(request.getDailyCapClicks())
                .build();
        
        AdsCampaign saved = campaignRepository.save(campaign);
        
        log.info("Campaign created: id={}, name={}", saved.getId(), saved.getName());
        
        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Campaign created successfully")
                .data(mapToDto(saved))
                .build();
    }

    /**
     * GET All Campaigns
     */
    @Transactional(readOnly = true)
    public ApiResponse getAllCampaigns() {
        
        List<AdsCampaign> campaigns = campaignRepository.findAll();
        
        List<AdsCampaignDto> dtos = campaigns.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(dtos)
                .build();
    }

    /**
     * GET Campaign by ID
     */
    @Transactional(readOnly = true)
    public ApiResponse getCampaignById(Long id) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(mapToDto(campaign))
                .build();
    }

    /**
     * UPDATE Campaign (PUT - full update)
     */
    @Transactional
    public ApiResponse updateCampaign(Long id, AdsCampaignDto.UpdateCampaign request) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        // Validate dates
        LocalDateTime startAt = request.getStartAt() != null ? request.getStartAt() : campaign.getStartAt();
        LocalDateTime endAt = request.getEndAt() != null ? request.getEndAt() : campaign.getEndAt();
        
        if (endAt != null && endAt.isBefore(startAt)) {
            throw new BadRequestException("End date must be after start date");
        }
        
        // Update fields
        if (request.getName() != null) {
            campaign.setName(request.getName());
        }
        
        if (request.getAdvertiser() != null) {
            campaign.setAdvertiser(request.getAdvertiser());
        }
        
        if (request.getStartAt() != null) {
            campaign.setStartAt(request.getStartAt());
        }
        
        if (request.getEndAt() != null) {
            campaign.setEndAt(request.getEndAt());
        }
        
        if (request.getDailyCapImpressions() != null) {
            campaign.setDailyCapImpressions(request.getDailyCapImpressions());
        }
        
        if (request.getDailyCapClicks() != null) {
            campaign.setDailyCapClicks(request.getDailyCapClicks());
        }
        
        campaign.setUpdatedAt(LocalDateTime.now());
        
        AdsCampaign updated = campaignRepository.save(campaign);
        
        log.info("Campaign updated: id={}, name={}", id, updated.getName());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Campaign updated successfully")
                .data(mapToDto(updated))
                .build();
    }

    /**
     * UPDATE Campaign Status (PATCH)
     */
    @Transactional
    public ApiResponse updateCampaignStatus(Long id, AdsCampaignDto.UpdateCampaignStatus request) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        CampaignStatus oldStatus = campaign.getStatus();
        CampaignStatus newStatus = request.getStatus();
        
        if (oldStatus.equals(newStatus)) {
            throw new BadRequestException("Campaign is already in " + newStatus + " status");
        }
        
        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);
        
        campaign.setStatus(newStatus);
        campaign.setUpdatedAt(LocalDateTime.now());
        
        AdsCampaign updated = campaignRepository.save(campaign);
        
        log.info("Campaign status changed: id={}, from={}, to={}", id, oldStatus, newStatus);
        
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Campaign status updated successfully")
                .data(mapToDto(updated))
                .build();
    }

    /**
     * DELETE Campaign
     */
    @Transactional
    public ApiResponse deleteCampaign(Long id) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        campaignRepository.deleteById(id);
        
        log.info("Campaign deleted: id={}, name={}", id, campaign.getName());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Campaign deleted successfully")
                .build();
    }

    /**
     * Validate status transitions
     */
    private void validateStatusTransition(CampaignStatus from, CampaignStatus to) {
        // Business rules for status transitions
        if (from == CampaignStatus.ENDED) {
            throw new BadRequestException("Cannot change status of ended campaign");
        }
    }

    /**
     * Entity → DTO mapping
     */
    private AdsCampaignDto mapToDto(AdsCampaign campaign) {
        return AdsCampaignDto.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .advertiser(campaign.getAdvertiser())
                .status(campaign.getStatus())
                .startAt(campaign.getStartAt())
                .endAt(campaign.getEndAt())
                .dailyCapImpressions(campaign.getDailyCapImpressions())
                .dailyCapClicks(campaign.getDailyCapClicks())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }
}