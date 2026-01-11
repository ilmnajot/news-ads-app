package uz.ilmnajot.newsadsapp.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import uz.ilmnajot.newsadsapp.entity.Category;
import uz.ilmnajot.newsadsapp.entity.CategoryTranslation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CategoryDto {

    private Long id;
    private Long parentId;
    private List<CategoryTranslationDto> translationsDtoList;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @Data
    public static class AddCategory{
        private Long parentId;
        private List<CategoryTranslationDto.AddCategoryTranslation> translationsDtoList;
    }

    @Data
    @Builder
    public static class CategoryPublicDto {
        private Long id;
        private Long parentId;
        private String lang;
        private String title;
        private String slug;
        private String description;
    }

}
