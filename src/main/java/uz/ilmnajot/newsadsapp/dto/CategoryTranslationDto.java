package uz.ilmnajot.newsadsapp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import uz.ilmnajot.newsadsapp.entity.Category;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryTranslationDto {

    private Long id;
    private CategoryDto categoryDto;
    private String lang;
    private String title;
    private String slug;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;



    @Data
    public static class AddCategoryTranslation{

    @NotBlank
    @Pattern(regexp = "uz|ru|en")
    private String lang;

    @NotBlank
    @Size(min = 2, max = 100)
    private String title;
    private String description;
    }
}
