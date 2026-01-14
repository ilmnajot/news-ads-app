package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.AdsCreativeDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;
import uz.ilmnajot.newsadsapp.entity.AdsCreative;
import uz.ilmnajot.newsadsapp.entity.AdsCreativeTranslation;
import uz.ilmnajot.newsadsapp.entity.Media;
import uz.ilmnajot.newsadsapp.enums.CreativeType;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.AdsCampaignRepository;
import uz.ilmnajot.newsadsapp.repository.AdsCreativeRepository;
import uz.ilmnajot.newsadsapp.repository.MediaRepository;
import uz.ilmnajot.newsadsapp.service.AdsCreativeService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsCreativeServiceImpl implements AdsCreativeService {

    private final AdsCreativeRepository creativeRepository;
    private final AdsCampaignRepository campaignRepository;
    private final MediaRepository mediaRepository;

    /**
     * CREATE Creative
     */
    @Transactional
    @Override
    public ApiResponse createCreative(AdsCreativeDto.CreateCreative request) {

        // Validate campaign
        AdsCampaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        // Validate type-specific fields
        validateCreativeData(
                request.getType(),
                request.getImageMediaId(),
                request.getHtmlSnippet());

        AdsCreative creative = AdsCreative.builder()
                .campaign(campaign)
                .type(request.getType())
                .landingUrl(request.getLandingUrl())
                .isActive(true)
                .build();

        // Set type-specific fields
        if (request.getType() == CreativeType.IMAGE) {
            Media media = mediaRepository.findById(request.getImageMediaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Media not found"));
            creative.setImageMedia(media);
        } else {
            creative.setHtmlSnippet(request.getHtmlSnippet());
        }

        // Save creative first
        creative = creativeRepository.save(creative);

        // Create translations
        for (Map.Entry<String, AdsCreativeDto.CreateTranslation> entry : request.getTranslations().entrySet()) {
            String lang = entry.getKey();
            AdsCreativeDto.CreateTranslation tr = entry.getValue();

            AdsCreativeTranslation translation = AdsCreativeTranslation.builder()
                    .creative(creative)
                    .lang(lang)
                    .title(tr.getTitle())
                    .altText(tr.getAltText())
                    .build();

            log.info("ino: {}", creative.getTranslations());
            if (creative.getTranslations() == null) {
                creative.setTranslations(new ArrayList<>());
            }
            creative.getTranslations().add(translation);

        }

        AdsCreative saved = creativeRepository.save(creative);

        log.info("Creative created: id={}, type={}, campaignId={}",
                saved.getId(), saved.getType(), campaign.getId());

        return ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Creative created successfully")
                .data(mapToDto(saved))
                .build();
    }

    /**
     * GET All Creatives
     */
    @Override
    public ApiResponse getAllCreatives() {

        List<AdsCreative> creatives = creativeRepository.findAll();

        List<AdsCreativeDto> dtos = creatives.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dtos)
                .build();
    }

    /**
     * GET Creative by ID
     */
    @Override
    public ApiResponse getCreativeById(Long id) {

        AdsCreative creative = creativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Creative not found"));

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(mapToDto(creative))
                .build();
    }

    /**
     * UPDATE Creative (PUT)
     */
    @Transactional
    @Override
    public ApiResponse updateCreative(Long id, AdsCreativeDto.UpdateCreative request) {

        AdsCreative creative = creativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Creative not found"));

        // Update fields
        if (request.getLandingUrl() != null) {
            creative.setLandingUrl(request.getLandingUrl());
        }

        if (request.getImageMediaId() != null && creative.getType() == CreativeType.IMAGE) {
            Media media = mediaRepository.findById(request.getImageMediaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Media not found"));
            creative.setImageMedia(media);
        }

        if (request.getHtmlSnippet() != null && creative.getType() == CreativeType.HTML) {
            creative.setHtmlSnippet(request.getHtmlSnippet());
        }

        if (request.getIsActive() != null) {
            creative.setIsActive(request.getIsActive());
        }

        // Update translations
        if (request.getTranslations() != null && !request.getTranslations().isEmpty()) {
            // Clear existing
            creative.getTranslations().clear();

            // Add new
            for (Map.Entry<String, AdsCreativeDto.CreateTranslation> entry : request.getTranslations().entrySet()) {
                String lang = entry.getKey();
                AdsCreativeDto.CreateTranslation tr = entry.getValue();

                AdsCreativeTranslation translation = AdsCreativeTranslation.builder()
                        .creative(creative)
                        .lang(lang)
                        .title(tr.getTitle())
                        .altText(tr.getAltText())
                        .build();

                creative.getTranslations().add(translation);
            }
        }

        creative.setUpdatedAt(LocalDateTime.now());

        AdsCreative updated = creativeRepository.save(creative);

        log.info("Creative updated: id={}", id);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Creative updated successfully")
                .data(mapToDto(updated))
                .build();
    }

    /**
     * DELETE Creative
     */
    @Transactional
    @Override
    public ApiResponse deleteCreative(Long id) {

        if (!creativeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Creative not found");
        }

        creativeRepository.deleteById(id);

        log.info("Creative deleted: id={}", id);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Creative deleted successfully")
                .build();
    }

    /**
     * Validate creative data based on type
     */
    private void validateCreativeData(CreativeType type, Long imageMediaId, String htmlSnippet) {
        if (type == CreativeType.IMAGE && imageMediaId == null) {
            throw new BadRequestException("Image media ID is required for IMAGE type");
        }
        if (type == CreativeType.HTML && (htmlSnippet == null || htmlSnippet.isEmpty())) {
            throw new BadRequestException("HTML snippet is required for HTML type");
        }
    }

    /**
     * Entity → DTO mapping
     */
    private AdsCreativeDto mapToDto(AdsCreative creative) {

        // Map translations
        Map<String, AdsCreativeDto.TranslationDto> translationMap = new HashMap<>();
        for (AdsCreativeTranslation tr : creative.getTranslations()) {
            translationMap.put(tr.getLang(), new AdsCreativeDto.TranslationDto(
                    tr.getLang(),
                    tr.getTitle(),
                    tr.getAltText()
            ));
        }

        return AdsCreativeDto.builder()
                .id(creative.getId())
                .campaignId(creative.getCampaign().getId())
                .campaignName(creative.getCampaign().getName())
                .type(creative.getType())
                .landingUrl(creative.getLandingUrl())
                .imageMediaId(creative.getImageMedia() != null ? creative.getImageMedia().getId() : null)
                .imageUrl(creative.getImageMedia() != null ? creative.getImageMedia().getUrl() : null)
                .htmlSnippet(creative.getHtmlSnippet())
                .isActive(creative.getIsActive())
                .translations(translationMap)
                .createdAt(creative.getCreatedAt())
                .updatedAt(creative.getUpdatedAt())
                .build();
    }
}