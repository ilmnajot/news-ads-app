package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
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
    public ApiResponse getAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return this.authService
                .getAllUsers(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/toggle-status/{userId}")
    public ApiResponse changeUserStatus(@PathVariable Long userId, @RequestParam boolean status) {
        return this.authService.changeUserStatus(userId, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-user/{userId}")
    public ApiResponse removeUser(@PathVariable Long userId) {
        return this.authService.removeUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RateLimit(limit = 10, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many register attempts")
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
    public ApiResponse login(@Valid @RequestBody UserDto.LoginDto dto) {
        return authService.login(dto);
    }

    /**
     * done
     *
     */
    @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many attempts")
    @PostMapping("/refresh")
    public ApiResponse refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
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
