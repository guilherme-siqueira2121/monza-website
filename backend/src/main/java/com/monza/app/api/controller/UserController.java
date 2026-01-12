package com.monza.app.api.controller;

import com.monza.app.api.dto.CreateUserRequest;
import com.monza.app.api.dto.ErrorResponse;
import com.monza.app.api.dto.UserResponse;
import com.monza.app.domain.User;
import com.monza.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// endpoints REST
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request.getUsername());

            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getUserCode(),
                    user.getRole(),
                    user.getCreatedAt()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao criar usuário"));
        }
    }

    // search user by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    UserResponse response = new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getUserCode(),
                            user.getRole(),
                            user.getCreatedAt()
                    );

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // busca por username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> {
                    UserResponse response = new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getUserCode(),
                            user.getRole(),
                            user.getCreatedAt()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // verifica se username existe
    @GetMapping("/check/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = userService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }
}

// -