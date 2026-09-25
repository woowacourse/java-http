package org.apache.catalina.resource;

import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceHandlerTest {

    @Test
    void servesResourceBytesWithContentType() throws IOException {
        final var response = new HttpResponse();
        new ResourceHandler().serve("/assets/img/error-404-monochrome.svg", response);
        final var output = new ByteArrayOutputStream();
        response.writeTo(output);

        try (final var resource = getClass().getResourceAsStream("/static/assets/img/error-404-monochrome.svg")) {
            assertThat(output.toString(StandardCharsets.UTF_8)).contains("Content-Type: image/svg+xml")
                    .endsWith(new String(resource.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    @Test
    void rejectsPathsOutsideStaticResources() throws IOException {
        final var response = new HttpResponse();
        new ResourceHandler().serve("/../login.html", response);
        final var output = new ByteArrayOutputStream();
        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8)).startsWith("HTTP/1.1 404 Not Found");
    }
}
