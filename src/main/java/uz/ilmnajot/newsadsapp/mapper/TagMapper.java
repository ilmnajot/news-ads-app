package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.TagDto;
import uz.ilmnajot.newsadsapp.entity.Tag;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagMapper {

    public TagDto toDto(Tag tag) {
        return TagDto
                .builder()
                .id(tag.getId())
                .code(tag.getCode())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .updatedBy(tag.getUpdatedBy())
                .createdBy(tag.getCreatedBy())
                .isActive(tag.getIsActive())
                .build();
    }

    public Tag toEntity(TagDto dto) {
        return Tag
                .builder()
                .code(dto.getCode())
                .build();
    }

    public List<TagDto> toDto(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        return tags
                .stream()
                .map(this::toDto)
                .toList();
    }
    public void toUpdate(Tag tag, TagDto.UpdateTag dto) {
        if (dto.getCode() != null) {
            tag.setCode(dto.getCode());
        }
        if (dto.getIsActive()!=null){
            tag.setIsActive(dto.getIsActive());
        }
    }
}
