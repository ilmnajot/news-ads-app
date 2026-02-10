package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.entity.Role;
import uz.ilmnajot.newsadsapp.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    // toDto
    public UserDto toDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .roleNames(user.getRoles() != null ? user.getRoles().stream()
                        .map(Role::getName)
                        .collect(java.util.stream.Collectors.toSet()) : null)
                .roleIds(user.getRoles() != null ? user.getRoles().stream()
                        .map(Role::getId)
                        .collect(java.util.stream.Collectors.toSet()) : null)
                .build();
    }

    // toEntity
    public User toEntity(UserDto.AddUserDto dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setIsActive(true);
        return user;
    }

    // toDto
    public List<UserDto> toDto(List<User> users) {
        if (users == null || users.isEmpty()) {
            return new ArrayList<>();
        }
        return users
                .stream()
                .map(this::toDto)
                .toList();
    }

    // toUpdate
    public void toUpdate(User user, UserDto.UpdateDto dto) {
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }
}
