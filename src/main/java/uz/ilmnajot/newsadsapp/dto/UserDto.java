package uz.ilmnajot.newsadsapp.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import uz.ilmnajot.newsadsapp.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class UserDto {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private Set<String> roleNames;
    private Set<Long> roleIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isActive;

    @Data
    public static class LoginDto {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

    }

    @Data
    public static class AddUserDto {
        private String fullName;
        private String username;
        private String email;
        private Set<Long> roleIds;
        private String password;
        private Boolean isActive = true;
    }

    @Data
    public static class UpdateDto {
        private String fullName;
        private String username;
        private String email;
        private Set<Long> roleIds;
    }
}
