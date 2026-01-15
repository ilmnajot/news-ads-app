package uz.ilmnajot.newsadsapp.service;

import org.springframework.data.domain.Pageable;
import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface AuthService {

    JwtResponse login(UserDto.LoginDto dto);

    JwtResponse refreshToken(String refreshToken);

    ApiResponse registerUser(UserDto.AddUserDto dto);

    ApiResponse getCurrentUser();

    ApiResponse changeCredentials(UserDto.UpdateDto dto, Long userId);

    ApiResponse getAllUsers(Pageable pageable);

    ApiResponse changeUserStatus(Long userId, boolean status);

    ApiResponse removeUser(Long userId);

    ApiResponse getAllRoles();
}
