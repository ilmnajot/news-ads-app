package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.entity.Role;
import uz.ilmnajot.newsadsapp.entity.User;
import uz.ilmnajot.newsadsapp.repository.RoleRepository;
import uz.ilmnajot.newsadsapp.repository.UserRepository;
import uz.ilmnajot.newsadsapp.security.JwtProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public JwtResponse login(UserDto.LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(accessToken))
                .refreshTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(refreshToken))
                .build();
    }

    public JwtResponse refreshToken(String refreshToken) {
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!tokenProvider.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = tokenProvider.generateAccessToken(userDetails);

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(newAccessToken))
                .refreshTokenExpireDate(this.tokenProvider.getExpirationDateFromToken(refreshToken))
                .build();
    }

    public ApiResponse registerUser(UserDto.AddUserDto dto) {
        log.info("ROLLAR {}", Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getAuthorities());
        Optional<User> userOptional = this.userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("User with phone number already exists.")
                    .build();
        }
        Set<Role> allByIds = this.roleRepository.findAllByIds(dto.getRoleIds());
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setIsActive(true);
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        user.setRoles(allByIds);
        User saved = this.userRepository.save(user);
        UserDto userDto = this.toDto(saved);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(userDto)
                .build();
    }
    private UserDto toDto(User user) {
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
                .build();
    }

}

