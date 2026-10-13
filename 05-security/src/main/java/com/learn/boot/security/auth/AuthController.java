package com.learn.boot.security.auth;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learn.boot.security.user.Role;
import com.learn.boot.security.user.UserService;
import com.learn.boot.security.user.UserService.UsernameTakenException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
public class AuthController {
    private final TokenService tokens;
    private final UserService users;

    public AuthController(TokenService tokens, UserService users) {
        this.tokens = tokens;
        this.users = users;
    }

    /** Public sign-up. New accounts only ever get USER: clients must not choose their own roles. */
    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody Registration request) {
        users.register(request.username(), request.password(), Set.of(Role.USER));
        return Map.of("username", request.username());
    }

    /** Reached only after HTTP Basic authentication succeeded (see SecurityConfig.tokenChain). */
    @PostMapping("/auth/token")
    public TokenResponse token(Authentication authentication) {
        return new TokenResponse(tokens.issue(authentication), "Bearer", tokens.ttlSeconds());
    }

    /** Who am I? Reads the validated JWT. */
    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("username", jwt.getSubject(), "roles", jwt.getClaimAsStringList("roles"), "expiresAt", jwt.getExpiresAt());
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ProblemDetail usernameTaken(UsernameTakenException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    public record Registration(@NotBlank @Size(min = 3, max = 50) String username,
                               @NotBlank @Size(min = 8, max = 100) String password) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }
}
