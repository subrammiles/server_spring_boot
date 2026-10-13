package com.learn.boot.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.learn.boot.security.user.AppUserRepository;
import com.learn.boot.security.user.Role;

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    AppUserRepository users;

    @Autowired
    PasswordEncoder encoder;

    @Test
    void registeredUserGetsRoleUserAndAHashedPassword() {
        assertThat(mvc.post().uri("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"meera\",\"password\":\"s3cret-pass\"}"))
                .hasStatus(HttpStatus.CREATED);

        var stored = users.findByUsername("meera").orElseThrow();
        assertThat(stored.getRoles()).containsExactly(Role.USER);
        assertThat(stored.getPasswordHash()).startsWith("{bcrypt}").doesNotContain("s3cret-pass");
        assertThat(encoder.matches("s3cret-pass", stored.getPasswordHash())).isTrue();

        assertThat(mvc.post().uri("/auth/token").with(httpBasic("meera", "s3cret-pass"))).hasStatusOk();
    }

    @Test
    void duplicateUsernameIs409AndWeakPasswordIs400() {
        assertThat(mvc.post().uri("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"whatever123\"}"))
                .hasStatus(HttpStatus.CONFLICT);
        assertThat(mvc.post().uri("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"bob\",\"password\":\"short\"}"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }
}
