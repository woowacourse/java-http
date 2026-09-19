package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseContentResolverTest {

    private final ResponseContentResolver resolver = new ResponseContentResolver();

    @Test
    void loginPathResolvesToLoginPage() throws IOException {
        final var response = resolver.resolve("/login");

        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            assertThat(resource).isNotNull();
            assertThat(response.contentType()).isEqualTo("text/html;charset=utf-8");
            assertThat(response.body()).isEqualTo(resource.readAllBytes());
        }
    }

    @Test
    void unknownPathKeepsDefaultResponse() throws IOException {
        final var response = resolver.resolve("/unknown.html");

        assertThat(response.contentType()).isEqualTo("text/html;charset=utf-8");
        assertThat(response.body()).isEqualTo("Hello world!".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void pathsOutsideAllowlistAreNotRead() throws IOException {
        final var response = resolver.resolve("/../login.html");

        assertThat(response.body()).isEqualTo("Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}
