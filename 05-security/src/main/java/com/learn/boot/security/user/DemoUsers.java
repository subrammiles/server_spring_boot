package com.learn.boot.security.user;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds two demo accounts at startup. Real apps never ship known passwords:
 * create admins through a migration or an admin tool, with passwords from a secret store.
 */
@Component
public class DemoUsers implements ApplicationRunner {
    private final AppUserRepository users;
    private final UserService userService;

    public DemoUsers(AppUserRepository users, UserService userService) {
        this.users = users;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.count() == 0) {
            userService.register("student", "student123", Set.of(Role.USER));
            userService.register("admin", "admin123", Set.of(Role.USER, Role.ADMIN));
        }
    }
}
