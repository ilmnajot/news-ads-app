package uz.ilmnajot.newsadsapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long id;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryTitle;
    private Long coverMediaId;
    private String coverMediaUrl;
    private NewsStatus status;
    private Boolean isFeatured;
    private Boolean isDeleted;
    private LocalDateTime publishAt;
    private LocalDateTime unpublishAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Map<String, NewsTranslationResponse> translations;
    private List<String> tags;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsTranslationResponse {
        private Long id;
        private String lang;
        private String title;
        private String slug;
        private String summary;
        private String content;
        private String metaTitle;
        private String metaDescription;
    }
}

