package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.Role;
import uz.ilmnajot.newsadsapp.entity.User;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.mapper.UserMapper;
import uz.ilmnajot.newsadsapp.repository.RoleRepository;
import uz.ilmnajot.newsadsapp.repository.UserRepository;
import uz.ilmnajot.newsadsapp.security.JwtProvider;
import uz.ilmnajot.newsadsapp.service.AuthService;
import uz.ilmnajot.newsadsapp.util.UserUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserUtil userUtil;

    @Override
    public JwtResponse login(UserDto.LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());
        String accessToken = this.tokenProvider.generateAccessToken(userDetails);
        String refreshToken = this.tokenProvider.generateRefreshToken(userDetails);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(accessToken))
                .refreshTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(refreshToken))
                .build();
    }

    public JwtResponse refreshToken(String refreshToken) {
        String username = this.tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if (!tokenProvider.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String newAccessToken = this.tokenProvider.generateAccessToken(userDetails);

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(newAccessToken))
                .refreshTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(refreshToken))
                .build();
    }

    public ApiResponse registerUser(UserDto.AddUserDto dto) {
        log.info("ROLLAR {}",
                Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getAuthorities());
        Optional<User> userOptional = this.userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("User with phone number already exists.")
                    .build();
        }
        Set<Role> allByIds = this.roleRepository.findAllByIds(dto.getRoleIds());
        User user = this.userMapper.toEntity(dto);
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        user.setRoles(allByIds);
        User saved = this.userRepository.save(user);
        UserDto userDto = this.userMapper.toDto(saved);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(userDto)
                .build();
    }

    @Override
    public ApiResponse getCurrentUser() {
        User user = this.userUtil.getCurrentUser();
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(userMapper.toDto(user))
                .build();
    }

    @Override
    public ApiResponse changeCredentials(UserDto.UpdateDto dto, Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        this.userMapper.toUpdate(user, dto);
        if (dto.getRoleIds() != null) {
            Set<Role> roleSet = roleRepository.findAllByIds(dto.getRoleIds());
            user.setRoles(roleSet);
        }
        user = this.userRepository.save(user);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Credentials updated successfully")
                .data(userMapper.toDto(user))
                .build();
    }

    @Override
    public ApiResponse getAllUsers(Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(pageable);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(this.userMapper.toDto(userPage.getContent()))
                .elements(userPage.getTotalElements())
                .pages(userPage.getTotalPages())
                .build();
    }

    @Override
    public ApiResponse changeUserStatus(Long userId, boolean status) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(status);
        user = this.userRepository.save(user);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("User status changed successfully")
                .data(userMapper.toDto(user))
                .build();
    }

    @Override
    public ApiResponse removeUser(Long userId) {
        try {
            this.userRepository.deleteById(userId);

            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .message("User deleted successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("User can not be deleted")
                    .build();
        }
    }

    @Override
    public ApiResponse getAllRoles() {
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(this.roleRepository.findAll().stream().filter(role -> !role.getDeleted()).toList())
                .build();
    }

}
