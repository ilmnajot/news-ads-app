package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.AdsCampaignDto;
import uz.ilmnajot.newsadsapp.entity.AdsCampaign;

@Component
public class AdsCampaignMapper {

    // toDto
    public AdsCampaignDto toDto(AdsCampaign campaign) {
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
