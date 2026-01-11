package uz.ilmnajot.newsadsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicAdDto {
    
    private Long id;
    private String type;  // IMAGE, HTML
    private String title;
    private String altText;
    private String imageUrl;  // IMAGE type uchun
    private String htmlSnippet;  // HTML type uchun
    private String landingUrl;
}