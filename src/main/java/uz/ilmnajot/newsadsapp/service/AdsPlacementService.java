package uz.ilmnajot.newsadsapp.service;

import uz.ilmnajot.newsadsapp.dto.AdsPlacementDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface AdsPlacementService {

    ApiResponse deletePlacement(Long id);
    ApiResponse updatePlacement(Long id, AdsPlacementDto.UpdatePlacement request);
    ApiResponse getPlacementById(Long id);
    ApiResponse getAllPlacements();
    ApiResponse createPlacement(AdsPlacementDto.CreatePlacement request);
}
