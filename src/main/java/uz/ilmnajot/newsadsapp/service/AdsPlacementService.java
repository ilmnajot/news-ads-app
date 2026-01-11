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
import uz.ilmnajot.newsadsapp.mapper.AdsPlacementMapper;
import uz.ilmnajot.newsadsapp.repository.AdsPlacementRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsPlacementService {

    private final AdsPlacementRepository placementRepository;
    private final AdsPlacementMapper adsPlacementMapper;

    /**
     * CREATE Placement
     */
    @Transactional
    public ApiResponse createPlacement(AdsPlacementDto.CreatePlacement request) {

        // Check if code already exists
        if (placementRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new BadRequestException("Placement with code '" + request.getCode() + "' already exists");
        }

        AdsPlacement entity = this.adsPlacementMapper.toEntity(request);
        entity.setIsActive(true);
        entity = placementRepository.save(entity);
        log.info("Placement created: code={}", entity.getCode());
        return ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Placement created successfully")
                .data(this.adsPlacementMapper.toDto(entity))
                .build();
    }

    /**
     * GET All Placements
     */
    @Transactional(readOnly = true)
    public ApiResponse getAllPlacements() {

        List<AdsPlacement> placements = placementRepository.findAll();

        List<AdsPlacementDto> dtos = placements
                .stream()
                .map(this.adsPlacementMapper::toDto)
                .collect(Collectors.toList());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
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
                .data(this.adsPlacementMapper.toDto(placement))
                .build();
    }

    /**
     * UPDATE Placement
     */
    @Transactional
    public ApiResponse updatePlacement(Long id, AdsPlacementDto.UpdatePlacement request) {

        AdsPlacement placement = placementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement not found"));
        if (request.getCode() != null) {
            String code = request.getCode();
            if (this.placementRepository.existsByCodeAndIsActiveTrue(code)) {
                return ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("This is already exists")
                        .build();
            }
            placement.setCode(request.getCode());
        }
        this.adsPlacementMapper.toUpdate(placement, request);
        placement = this.placementRepository.save(placement);

        log.info("Placement updated: id={}, code={}", id, placement.getCode());
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(this.adsPlacementMapper.toDto(placement))
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

}