package edu.kudago.controller;

import edu.kudago.dto.AccountLoginRequestDto;
import edu.kudago.dto.AccountRegistrationRequestDto;
import edu.kudago.dto.AuthResponseDto;
import edu.kudago.dto.PasswordResetRequestDto;
import edu.kudago.exceptions.RoleNotFoundException;
import edu.kudago.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authorization", description = "Authorization operations")
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;
    private static final Integer CLEAR_COOKIE = 7 * 24 * 60 * 60;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully registered new user"),
            @ApiResponse(responseCode = "400", description = "Error during registration")
    })
    public AuthResponseDto register(@RequestBody @Valid AccountRegistrationRequestDto request, HttpServletResponse response)
            throws RoleNotFoundException {
        var result = authService.register(request.email().trim(), request.nickname().trim(), request.password().trim(), request.firstName().trim(), request.lastName().trim());
        addAuthCookie(response, result.getSecond());
        return new AuthResponseDto(result.getFirst().getNickname(), result.getFirst().getRole().getName().getDescription());
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Login error")
    })
    public AuthResponseDto login(@RequestBody @Valid AccountLoginRequestDto request, HttpServletResponse response) {
        var result = authService.login(request.email().trim(), request.password().trim(), request.rememberMe());
        addAuthCookie(response, result.getSecond());
        return new AuthResponseDto(result.getFirst().getNickname(), result.getFirst().getRole().getName().getDescription());
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        clearAuthCookie(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "400", description = "Reset error")
    })
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordResetRequestDto request) {
        authService.resetPassword(request.getEmail().trim(), request.getNewPassword().trim(), request.getCode().trim());
        return ResponseEntity.ok("Password successfully reset");
    }

    private void addAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(CLEAR_COOKIE);
        response.addCookie(cookie);
    }

    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
