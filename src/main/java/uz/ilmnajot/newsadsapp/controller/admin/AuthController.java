package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.dto.UserDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.dto.JwtResponse;
import uz.ilmnajot.newsadsapp.service.AuthService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many login attempts")
    @PostMapping("/register")
    public HttpEntity<ApiResponse> registerUser(@RequestBody UserDto.AddUserDto dto) {
        ApiResponse apiResponse = this.authService.registerUser(dto);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
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
    * */
    @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES, message = "Too many login attempts")
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestParam String refreshToken) {
        JwtResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * need to ask if server side logout is needed
     * if client-side logout is enough, then we don't need server side logout
     * */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // In a stateless JWT system, logout is handled client-side by removing the token
        // For server-side logout, you'd need a token blacklist (Redis)
        return ResponseEntity.noContent().build();
    }
}

