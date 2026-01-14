package uz.ilmnajot.newsadsapp.service;

import uz.ilmnajot.newsadsapp.dto.AdsCampaignDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface AdsCampaignService {
    ApiResponse createCampaign(AdsCampaignDto.CreateCampaign request);
    ApiResponse getAllCampaigns();
    ApiResponse getCampaignById(Long id);
    ApiResponse updateCampaign(Long id, AdsCampaignDto.UpdateCampaign request);
    ApiResponse updateCampaignStatus(Long id, AdsCampaignDto.UpdateCampaignStatus request);
    ApiResponse deleteCampaign(Long id);

}
