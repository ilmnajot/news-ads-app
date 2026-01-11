package uz.ilmnajot.newsadsapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ilmnajot.newsadsapp.enums.CreativeType;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCreativeDto {
    
    private Long id;
    private Long campaignId;
    private String campaignName;
    private CreativeType type;
    private String landingUrl;
    private Long imageMediaId;
    private String imageUrl;
    private String htmlSnippet;
    private Boolean isActive;
    private Map<String, TranslationDto> translations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslationDto {
        private String lang;
        private String title;
        private String altText;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCreative {
        
        @NotNull(message = "Campaign ID is required")
        private Long campaignId;
        
        @NotNull(message = "Type is required")
        private CreativeType type;
        
        @Size(max = 500)
        private String landingUrl;
        
        // IMAGE type uchun
        private Long imageMediaId;
        
        // HTML type uchun
        private String htmlSnippet;
        
        // Translations: {"uz": {...}, "ru": {...}, "en": {...}}
        @NotEmpty(message = "Translations are required")
        private Map<String, CreateTranslation> translations;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTranslation {
        
        @NotBlank(message = "Language is required")
        @Pattern(regexp = "uz|ru|en")
        private String lang;
        
        @Size(max = 200)
        private String title;
        
        @Size(max = 200)
        private String altText;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCreative {
        
        @Size(max = 500)
        private String landingUrl;
        
        private Long imageMediaId;
        
        private String htmlSnippet;
        
        private Boolean isActive;
        
        private Map<String, CreateTranslation> translations;
    }
}