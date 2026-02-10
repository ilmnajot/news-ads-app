package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.AdsAssignmentDto;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;

@Component
public class AdsAssignmentMapper {
    // toDto
    public AdsAssignmentDto toDto(AdsAssignment assignment) {
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
