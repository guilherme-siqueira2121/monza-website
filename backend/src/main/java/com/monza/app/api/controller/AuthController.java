package com.monza.app.api.controller;

import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.LoginRequest;
import com.monza.app.api.dto.RegisterRequest;
import com.monza.app.api.dto.AuthResponse;
import com.monza.app.persistence.entity.UserEntity;
import com.monza.app.security.CustomUserDetailsService;
import com.monza.app.security.JwtService;
import com.monza.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtService jwtService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            var user = userService.createUserWithPassword(
                    request.getUsername(),
                    request.getPassword()
            );

            String accessToken = jwtService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
            String refreshToken = jwtService.generateRefreshToken(
                    user.getId(),
                    user.getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new AuthResponse(
                            accessToken,
                            refreshToken,
                            user.getId(),
                            user.getUsername(),
                            user.getUserCode(),
                            user.getRole()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserEntity user = userDetailsService.loadUserEntityByUsername(request.getUsername());

            String accessToken = jwtService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
            String refreshToken = jwtService.generateRefreshToken(
                    user.getId(),
                    user.getUsername()
            );

            return ResponseEntity.ok(
                    new AuthResponse(
                            accessToken,
                            refreshToken,
                            user.getId(),
                            user.getUsername(),
                            user.getUserCode(),
                            user.getRole()
                    )
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Usuário ou senha incorretos"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
                String token = refreshToken.substring(7);

                if (jwtService.validateToken(token)) {
                    String username = jwtService.extractUsername(token);
                    Long userId = jwtService.extractUserId(token);
                    UserEntity user = userDetailsService.loadUserEntityByUsername(username);

                    String newAccessToken = jwtService.generateToken(
                            userId,
                            username,
                            user.getRole()
                    );

                    return ResponseEntity.ok(
                            new AuthResponse(
                                    newAccessToken,
                                    token,
                                    user.getId(),
                                    user.getUsername(),
                                    user.getUserCode(),
                                    user.getRole()
                            )
                    );
                }
            }

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token de refresh inválido"));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Erro ao renovar token"));
        }
    }
}