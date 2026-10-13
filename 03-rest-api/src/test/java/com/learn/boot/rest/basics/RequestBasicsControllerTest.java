package com.learn.boot.rest.basics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import jakarta.servlet.http.Cookie;

@WebMvcTest({RequestBasicsController.class, LegacyController.class})
class RequestBasicsControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void queryParameters() {
        assertThat(mvc.get().uri("/basics/query?data=hi")).hasBodyTextEqualTo("You sent data = hi");
        assertThat(mvc.get().uri("/basics/query2?data=a")).hasBodyTextEqualTo("data = a, data2 = none");
        assertThat(mvc.get().uri("/basics/query")).hasStatus(HttpStatus.BAD_REQUEST);   // required param missing
        assertThat(mvc.get().uri("/basics/all-params?a=1&b=2")).bodyJson().extractingPath("$.b").isEqualTo("2");
    }

    @Test
    void jsonBodyAndBodyPlusQuery() {
        String json = "{\"id\":1,\"name\":\"Subbu\",\"salary\":100}";
        assertThat(mvc.post().uri("/basics/employees").contentType(MediaType.APPLICATION_JSON).content(json))
                .hasBodyTextEqualTo("Data of Subbu is saved");
        assertThat(mvc.post().uri("/basics/employees-with-query").param("department", "R&D").contentType(MediaType.APPLICATION_JSON).content(json))
                .hasBodyTextEqualTo("Data of Subbu is saved to R&D");
    }

    @Test
    void headersAndCookies() {
        assertThat(mvc.get().uri("/basics/whoami").header("User-Agent", "curl").header("X-Tenant", "acme")
                .cookie(new Cookie("session", "abc")))
                .bodyJson().isLenientlyEqualTo("{\"userAgent\":\"curl\",\"tenant\":\"acme\",\"session\":\"abc\"}");
    }

    @Test
    void responseEntityAndLegacyController() {
        assertThat(mvc.get().uri("/basics/custom-response")).hasStatus(HttpStatus.ACCEPTED).hasHeader("X-Processing-Time", "42ms");
        assertThat(mvc.get().uri("/basics/balance?userId=5")).hasBodyTextEqualTo("For user ID = 5 balance is 100");
        assertThat(mvc.get().uri("/basics/legacy")).hasBodyTextEqualTo("Hello from a @Controller + @ResponseBody method");
    }
}
