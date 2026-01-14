package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.AdsCampaignDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;
import uz.ilmnajot.newsadsapp.enums.AdsComStatus;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.mapper.AdsCampaignMapper;
import uz.ilmnajot.newsadsapp.repository.AdsCampaignRepository;
import uz.ilmnajot.newsadsapp.service.AdsCampaignService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsCampaignServiceImpl implements AdsCampaignService {

    private final AdsCampaignRepository campaignRepository;
    private final AdsCampaignMapper campaignMapper;

    /**
     * CREATE Campaign
     */
    @Override
    @Transactional
    public ApiResponse createCampaign(AdsCampaignDto.CreateCampaign request) {
        
        // Validate dates
        if (request.getEndAt() != null && request.getEndAt().isBefore(request.getStartAt())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        AdsCampaign campaign = AdsCampaign.builder()
                .name(request.getName())
                .advertiser(request.getAdvertiser())
                .status(AdsComStatus.DRAFT)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .dailyCapImpressions(request.getDailyCapImpressions())
                .dailyCapClicks(request.getDailyCapClicks())
                .build();
        
        AdsCampaign saved = campaignRepository.save(campaign);
        
        log.info("Campaign created: id={}, name={}", saved.getId(), saved.getName());
        
        return ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Campaign created successfully")
                .data(this.campaignMapper.toDto(saved))
                .build();
    }

    /**
     * GET All Campaigns
     */
    @Override
    public ApiResponse getAllCampaigns() {
        
        List<AdsCampaign> campaigns = campaignRepository.findAll();
        
        List<AdsCampaignDto> dtos = campaigns.stream()
                .map(this.campaignMapper::toDto)
                .collect(Collectors.toList());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dtos)
                .build();
    }

    /**
     * GET Campaign by ID
     */
   @Override
    public ApiResponse getCampaignById(Long id) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(this.campaignMapper.toDto(campaign))
                .build();
    }

    /**
     * UPDATE Campaign (PUT - full update)
     */
    @Transactional
    @Override
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
                .status(HttpStatus.OK)
                .message("Campaign updated successfully")
                .data(this.campaignMapper.toDto(updated))
                .build();
    }

    /**
     * UPDATE Campaign Status (PATCH)
     */
    @Transactional
    @Override
    public ApiResponse updateCampaignStatus(Long id, AdsCampaignDto.UpdateCampaignStatus request) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        AdsComStatus oldStatus = campaign.getStatus();
        AdsComStatus newStatus = request.getStatus();
        
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
                .status(HttpStatus.OK)
                .message("Campaign status updated successfully")
                .data(this.campaignMapper.toDto(updated))
                .build();
    }

    /**
     * DELETE Campaign
     */
    @Transactional
    @Override
    public ApiResponse deleteCampaign(Long id) {
        
        AdsCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        campaignRepository.deleteById(id);
        
        log.info("Campaign deleted: id={}, name={}", id, campaign.getName());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Campaign deleted successfully")
                .build();
    }

    /**
     * Validate status transitions
     */
    private void validateStatusTransition(AdsComStatus from, AdsComStatus to) {
        // Business rules for status transitions
        if (from == AdsComStatus.ENDED) {
            throw new BadRequestException("Cannot change status of ended campaign");
        }
    }

}