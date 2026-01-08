package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.CategoryDto;
import uz.ilmnajot.newsadsapp.dto.CategoryTranslationDto;
import uz.ilmnajot.newsadsapp.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category category) {

        List<CategoryTranslationDto> translationDtos = category.getTranslations().stream()
                .map(t -> CategoryTranslationDto.builder()
                        .id(t.getId())
                        .lang(t.getLang())
                        .title(t.getTitle())
                        .slug(t.getSlug())
                        .description(t.getDescription())
                        .createdAt(t.getCreatedAt())
                        .updatedAt(t.getUpdatedAt())
                        .build())
                .toList();

        return CategoryDto.builder()
                .id(category.getId())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .translationsDtoList(translationDtos)
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
