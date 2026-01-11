package uz.ilmnajot.newsadsapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsPlacementDto {
    
    private Long id;
    private String code;
    private String title;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePlacement {
        
        @NotBlank(message = "Code is required")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Code must contain only lowercase letters, numbers and hyphens")
        @Size(min = 3, max = 100)
        private String code;
        
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 200)
        private String title;
        
        private String description;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePlacement {
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Code must contain only lowercase letters, numbers and hyphens")
        @Size(min = 3, max = 100)
        private String code;
        @Size(min = 3, max = 200)
        private String title;
        
        private String description;
        
        private Boolean isActive;
    }
}