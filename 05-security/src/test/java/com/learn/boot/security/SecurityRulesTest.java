package com.learn.boot.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import tools.jackson.databind.json.JsonMapper;

/**
 * 401 Unauthorized = "who are you?" (no or invalid credentials)
 * 403 Forbidden    = "I know who you are, and you may not do this"
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityRulesTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JsonMapper json;

    @Test
    void publicEndpointNeedsNoToken() {
        assertThat(mvc.get().uri("/api/courses")).hasStatusOk();
    }

    @Test
    void protectedEndpointWithoutTokenIs401() {
        var result = mvc.get().uri("/api/me").exchange();
        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        // Spring Security 7 also advertises RFC 9728 metadata: Bearer resource_metadata="..."
        assertThat(result.getResponse().getHeader(HttpHeaders.WWW_AUTHENTICATE)).startsWith("Bearer");
    }

    @Test
    void userRoleCannotCreateCourses() {
        // jwt() fakes an already-validated token: tests focus on authorization, not crypto
        assertThat(mvc.post().uri("/api/courses").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Hacking 101\"}"))
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    void adminRoleCanCreateCourses() {
        assertThat(mvc.post().uri("/api/courses").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Spring Authorization Server\"}"))
                .hasStatus(HttpStatus.CREATED);
    }

    @Test
    void ownershipRuleOnServiceMethod() {
        assertThat(mvc.get().uri("/api/users/student/enrolments").with(jwt().jwt(j -> j.subject("student"))))
                .hasStatusOk();
        assertThat(mvc.get().uri("/api/users/admin/enrolments").with(jwt().jwt(j -> j.subject("student"))))
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    void wrongPasswordGetsNoToken() {
        assertThat(mvc.post().uri("/auth/token").with(httpBasic("admin", "wrong"))).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void endToEndLoginThenUseTheRealToken() throws Exception {
        var login = mvc.post().uri("/auth/token").with(httpBasic("admin", "admin123")).exchange();
        assertThat(login).hasStatusOk();
        String token = json.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();

        assertThat(mvc.get().uri("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .hasStatusOk()
                .bodyJson().extractingPath("$.roles").asArray().containsExactlyInAnyOrder("USER", "ADMIN");

        assertThat(mvc.delete().uri("/api/courses/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void tamperedTokenIsRejected() throws Exception {
        var login = mvc.post().uri("/auth/token").with(httpBasic("student", "student123")).exchange();
        String token = json.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        assertThat(mvc.get().uri("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + tampered))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void corsPreflightFromTheAllowedFrontend() {
        assertThat(mvc.options().uri("/api/courses")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173");
        assertThat(mvc.options().uri("/api/courses")
                .header(HttpHeaders.ORIGIN, "https://evil.example")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .hasStatus(HttpStatus.FORBIDDEN);
    }
}
