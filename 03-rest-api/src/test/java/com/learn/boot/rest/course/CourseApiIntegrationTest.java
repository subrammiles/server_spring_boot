package com.learn.boot.rest.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

/** END-TO-END: the real app on a random port, real HTTP, real service. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DirtiesContext   // this test changes the in-memory data
class CourseApiIntegrationTest {

    @Autowired
    RestTestClient client;

    @Test
    void fullCrudLifecycle() {
        URI location = client.post().uri("/api/courses").contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"title":"Kubernetes","category":"devops","durationHours":20,"price":2499.00}""")
                .exchange()
                .expectStatus().isCreated()
                .returnResult(String.class).getResponseHeaders().getLocation();
        assertThat(location).isNotNull();

        client.get().uri(location).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.title").isEqualTo("Kubernetes");

        client.patch().uri(location).contentType(MediaType.APPLICATION_JSON).body("{\"price\": 1999.00}")
                .exchange()
                .expectBody().jsonPath("$.price").isEqualTo(1999.00).jsonPath("$.title").isEqualTo("Kubernetes");

        client.delete().uri(location).exchange().expectStatus().isNoContent();
        client.get().uri(location).exchange().expectStatus().isNotFound();
    }

    @Test
    void searchPagingAndSorting() {
        client.get().uri("/api/courses?category=backend&sort=price&size=1").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(2)
                .jsonPath("$.content[0].title").isEqualTo("REST API Design");
    }

    @Test
    void duplicateTitleIs409() {
        client.post().uri("/api/courses").contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"title":"REST API Design","category":"backend","durationHours":8,"price":999.00}""")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void openApiDocumentIsGenerated() {
        client.get().uri("/v3/api-docs").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.paths['/api/courses']").exists();
    }
}
