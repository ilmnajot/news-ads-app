package uz.ilmnajot.newsadsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithLangDto {
    
    private Long id;
    private Long parentId;
    private Boolean isActive;

    private String lang;
    private String title;
    private String slug;
    private String description;

    private List<CategoryWithLangDto> children;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}