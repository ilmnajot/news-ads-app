package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.AdsPlacementDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsPlacement;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.AdsPlacementRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsPlacementService {

    private final AdsPlacementRepository placementRepository;

    /**
     * CREATE Placement
     */
    @Transactional
    public ApiResponse createPlacement(AdsPlacementDto.CreatePlacement request) {
        
        // Check if code already exists
        if (placementRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Placement with code '" + request.getCode() + "' already exists");
        }
        
        AdsPlacement placement = AdsPlacement.builder()
                .code(request.getCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .isActive(true)
                .build();
        
        AdsPlacement saved = placementRepository.save(placement);
        
        log.info("Placement created: code={}", saved.getCode());
        
        return ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Placement created successfully")
                .data(mapToDto(saved))
                .build();
    }

    /**
     * GET All Placements
     */
    @Transactional(readOnly = true)
    public ApiResponse getAllPlacements() {
        
        List<AdsPlacement> placements = placementRepository.findAll();
        
        List<AdsPlacementDto> dtos = placements.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK
                )
                .message("Success")
                .data(dtos)
                .build();
    }

    /**
     * GET Placement by ID
     */
    @Transactional(readOnly = true)
    public ApiResponse getPlacementById(Long id) {
        
        AdsPlacement placement = placementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement not found"));
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(mapToDto(placement))
                .build();
    }

    /**
     * UPDATE Placement
     */
    @Transactional
    public ApiResponse updatePlacement(Long id, AdsPlacementDto.UpdatePlacement request) {
        
        AdsPlacement placement = placementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement not found"));
        
        if (request.getTitle() != null) {
            placement.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            placement.setDescription(request.getDescription());
        }
        
        if (request.getIsActive() != null) {
            placement.setIsActive(request.getIsActive());
        }
        
        placement.setUpdatedAt(LocalDateTime.now());
        
        AdsPlacement updated = placementRepository.save(placement);
        
        log.info("Placement updated: id={}, code={}", id, updated.getCode());
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Placement updated successfully")
                .data(mapToDto(updated))
                .build();
    }

    /**
     * DELETE Placement
     */
    @Transactional
    public ApiResponse deletePlacement(Long id) {
        
        if (!placementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Placement not found");
        }
        
        placementRepository.deleteById(id);
        
        log.info("Placement deleted: id={}", id);
        
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Placement deleted successfully")
                .build();
    }

    /**
     * Entity → DTO mapping
     */
    private AdsPlacementDto mapToDto(AdsPlacement placement) {
        return AdsPlacementDto.builder()
                .id(placement.getId())
                .code(placement.getCode())
                .title(placement.getTitle())
                .description(placement.getDescription())
                .isActive(placement.getIsActive())
                .createdAt(placement.getCreatedAt())
                .updatedAt(placement.getUpdatedAt())
                .build();
    }
}