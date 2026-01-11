package uz.ilmnajot.newsadsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsPublicResponse {
    
    private Long id;
    
    // Translation fields (current language only)
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String metaTitle;
    private String metaDescription;
    
    // Media
    private String coverImageUrl;
    
    // Category
    private Long categoryId;
    private String categoryTitle;
    private String categorySlug;
    
    // Tags
    private Set<String> tags;
    
    // Metadata
    private Boolean isFeatured;
    private LocalDateTime publishedAt;
}