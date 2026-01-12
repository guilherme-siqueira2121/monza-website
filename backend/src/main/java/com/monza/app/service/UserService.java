package com.monza.app.service;

import com.monza.app.domain.User;
import com.monza.app.persistence.entity.UserEntity;
import com.monza.app.persistence.mapper.UserMapper;
import com.monza.app.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public UserService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // create user with password
    @Transactional
    public User createUserWithPassword(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username já existe");
        }

        String userCode = generateUniqueUserCode();
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(username.trim(), userCode, encodedPassword);
        user.validate();

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);

        return userMapper.toDomain(saved);
    }

    // create user with default password
    @Transactional
    public User createUser(String username) {
        return createUserWithPassword(username, "changeme123");
    }

    // generate unique code
    private String generateUniqueUserCode() {
        String code;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            code = "USR" + String.format("%06d", random.nextInt(1000000));
            attempts++;

            if (attempts >= maxAttempts) {
                throw new IllegalArgumentException("Não foi possível gerar código único");
            }
        } while (userRepository.existsByUserCode(code));

        return code;
    }

    // search user by id
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }

    // search user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}

// -