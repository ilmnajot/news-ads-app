package uz.ilmnajot.newsadsapp.service;

import uz.ilmnajot.newsadsapp.dto.AdsCreativeDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface AdsCreativeService {
    ApiResponse createCreative(AdsCreativeDto.CreateCreative request);
    ApiResponse getAllCreatives();
    ApiResponse getCreativeById(Long id);
    ApiResponse updateCreative(Long id, AdsCreativeDto.UpdateCreative request);
    ApiResponse deleteCreative(Long id);
}
