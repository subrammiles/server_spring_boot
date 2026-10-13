package com.learn.boot.security.user;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(String username, String rawPassword, Set<Role> roles) {
        if (users.existsByUsername(username)) {
            throw new UsernameTakenException(username);
        }
        return users.save(new AppUser(username, passwordEncoder.encode(rawPassword), roles));
    }

    public static class UsernameTakenException extends RuntimeException {
        public UsernameTakenException(String username) {
            super("Username '" + username + "' is already taken");
        }
    }
}
