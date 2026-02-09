package uz.ilmnajot.newsadsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class NewsCreateRequest {
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private Long coverMediaId;
    //draft
    @NotNull(message = "Status is required")
    private String status;
    
    private Boolean isFeatured = false;
    
    private LocalDateTime publishAt;
    private LocalDateTime unpublishAt;
    
    @NotNull(message = "Translations are required")
    @Size(min = 1, message = "At least one translation is required")
    private Map<String, NewsTranslationRequest> translations;
    
    private Set<String> tagCodes;
    
    @Data
    public static class NewsTranslationRequest {
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 500, message = "Title must be between 3 and 500 characters")
        private String title;
        
        private String slug;
        
        @Size(max = 5000, message = "Summary must not exceed 5000 characters")
        private String summary;
        
        @NotBlank(message = "Content is required")
        private String content;
        
        private String metaTitle;
        private String metaDescription;
    }
}

