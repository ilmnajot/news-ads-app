package uz.ilmnajot.newsadsapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ilmnajot.newsadsapp.enums.CampaignStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCampaignDto {
    
    private Long id;
    private String name;
    private String advertiser;
    private CampaignStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer dailyCapImpressions;
    private Integer dailyCapClicks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCampaign {
        
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 200)
        private String name;
        
        private String advertiser;
        
        @NotNull(message = "Start date is required")
        private LocalDateTime startAt;
        
        private LocalDateTime endAt;
        
        @Min(0)
        private Integer dailyCapImpressions;
        
        @Min(0)
        private Integer dailyCapClicks;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCampaign {
        
        @Size(min = 3, max = 200)
        private String name;
        
        private String advertiser;
        
        private LocalDateTime startAt;
        
        private LocalDateTime endAt;
        
        @Min(0)
        private Integer dailyCapImpressions;
        
        @Min(0)
        private Integer dailyCapClicks;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCampaignStatus {
        
        @NotNull(message = "Status is required")
        private CampaignStatus status;
    }
}