package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.AdsPlacementDto;
import uz.ilmnajot.newsadsapp.entity.AdsPlacement;

@Component
public class AdsPlacementMapper {

    public AdsPlacementDto toDto(AdsPlacement ads) {
        return AdsPlacementDto.builder()
                .id(ads.getId())
                .code(ads.getCode())
                .title(ads.getTitle())
                .description(ads.getDescription())
                .isActive(ads.getIsActive())
                .createdAt(ads.getCreatedAt())
                .updatedAt(ads.getUpdatedAt())
                .createdBy(ads.getCreatedBy())
                .updatedBy(ads.getUpdatedBy())
                .build();
    }

    public AdsPlacement toEntity(AdsPlacementDto.CreatePlacement dto) {
        if (dto == null) return null;
        return AdsPlacement.builder()
                .code(dto.getCode())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }

    public void toUpdate(AdsPlacement entity, AdsPlacementDto.UpdatePlacement dto) {
        if (dto == null) return;
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }
}
