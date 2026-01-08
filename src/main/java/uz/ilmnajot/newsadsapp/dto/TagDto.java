package uz.ilmnajot.newsadsapp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TagDto {
    private Long id;
    private String code;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;


    @Data
    public static class AddTag{
        @NotBlank(message = "Tag code is required")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Only lowercase, numbers and hyphens allowed")
        @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
        private String code;
    }
    @Data
    public static class UpdateTag{
        @NotBlank(message = "Tag code is required")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Only lowercase, numbers and hyphens allowed")
        @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
        private String code;

        private Boolean isActive;
    }
}
