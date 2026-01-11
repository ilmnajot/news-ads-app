package uz.ilmnajot.newsadsapp.dto;

import jakarta.validation.constraints.*;
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
public class AdsAssignmentDto {
    
    private Long id;
    private Long placementId;
    private String placementCode;
    private String placementTitle;
    private Long campaignId;
    private String campaignName;
    private Long creativeId;
    private Integer weight;
    private List<String> langFilter;
    private List<Long> categoryFilter;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAssignment {
        
        @NotNull(message = "Placement ID is required")
        private Long placementId;
        
        @NotNull(message = "Campaign ID is required")
        private Long campaignId;
        
        @NotNull(message = "Creative ID is required")
        private Long creativeId;
        
        @NotNull(message = "Weight is required")
        @Min(value = 0, message = "Weight must be between 0 and 100")
        @Max(value = 100, message = "Weight must be between 0 and 100")
        private Integer weight;
        
        private List<String> langFilter;  // ["uz", "ru"] yoki null
        
        private List<Long> categoryFilter;  // [1, 2, 3] yoki null
        
        @NotNull(message = "Start date is required")
        private LocalDateTime startAt;
        
        private LocalDateTime endAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAssignment {
        
        @Min(value = 0)
        @Max(value = 100)
        private Integer weight;
        
        private List<String> langFilter;
        
        private List<Long> categoryFilter;
        
        private LocalDateTime startAt;
        
        private LocalDateTime endAt;
        
        private Boolean isActive;
    }
}