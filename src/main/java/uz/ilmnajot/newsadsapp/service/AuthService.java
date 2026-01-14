package uz.ilmnajot.newsadsapp.service;

import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface AuthService {

    JwtResponse login(UserDto.LoginDto dto);
    JwtResponse refreshToken(String refreshToken);
    ApiResponse registerUser(UserDto.AddUserDto dto);
}
