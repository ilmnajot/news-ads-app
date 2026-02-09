package uz.ilmnajot.newsadsapp.service;

import org.springframework.data.domain.Pageable;
import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

import java.util.UUID;

public interface AuthService {

    ApiResponse login(UserDto.LoginDto dto);

    ApiResponse refreshToken(String refreshToken);

    ApiResponse registerUser(UserDto.AddUserDto dto);

    ApiResponse getCurrentUser();

    ApiResponse changeCredentials(UserDto.UpdateDto dto, UUID userId);

    ApiResponse getAllUsers(Pageable pageable);

    ApiResponse changeUserStatus(UUID userId, boolean status);

    ApiResponse removeUser(UUID userId);

    ApiResponse getAllRoles();
}
