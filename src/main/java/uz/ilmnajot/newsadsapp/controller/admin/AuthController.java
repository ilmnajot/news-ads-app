package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.User;
import uz.ilmnajot.newsadsapp.service.AuthService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/me")
    public ApiResponse getCurrentUser() {
        return authService.getCurrentUser();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/change-credentials/{userId}")
    public ApiResponse changeCredentials(@PathVariable Long userId,
            @RequestBody UserDto.UpdateDto dto) {
        return this.authService.changeCredentials(dto, userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-users")
    public HttpEntity<ApiResponse> getAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ApiResponse apiResponse = this.authService
                .getAllUsers(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/toggle-status/{userId}")
    public HttpEntity<ApiResponse> changeUserStatus(@PathVariable Long userId, @RequestParam boolean status) {
        ApiResponse apiResponse = this.authService.changeUserStatus(userId, status);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-user/{userId}")
    public HttpEntity<ApiResponse> removeUser(@PathVariable Long userId) {
        ApiResponse apiResponse = this.authService.removeUser(userId);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many login attempts")
    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody UserDto.AddUserDto dto) {
        return this.authService.registerUser(dto);
    }

    /**
     * LOGIN - 5 attempts per minute
     * CRITICAL: Prevents brute force attacks
     */

    @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many login attempts")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserDto.LoginDto dto) {
        JwtResponse response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * done
     *
     */
    @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many login attempts")
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestParam String refreshToken) {
        JwtResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * need to ask if server side logout is needed
     * if client-side logout is enough, then we don't need server side logout
     *
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-roles")
    public ApiResponse getRoles() {
        return this.authService.getAllRoles();
    }
}
