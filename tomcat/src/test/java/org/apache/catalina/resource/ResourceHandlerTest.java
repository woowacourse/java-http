package org.apache.catalina.resource;

import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
    void preservesPngBodyBytes() throws IOException {
        final byte[] expectedBody;
        try (final var resource = getClass().getResourceAsStream("/static/test-image.png")) {
            assertThat(resource).isNotNull();
            expectedBody = resource.readAllBytes();
        }
        final var response = new HttpResponse();
        new ResourceHandler().serve("/test-image.png", response);
        final var output = new ByteArrayOutputStream();
        response.writeTo(output);

        final byte[] actualResponse = output.toByteArray();
        // ISO-8859-1은 바이트와 문자 위치가 일치하므로 헤더 경계를 찾는 데만 사용한다.
        final int headerEnd = new String(actualResponse, StandardCharsets.ISO_8859_1).indexOf("\r\n\r\n");
        assertThat(headerEnd).isGreaterThanOrEqualTo(0);
        final String headers = new String(actualResponse, 0, headerEnd, StandardCharsets.US_ASCII);
        final byte[] actualBody = Arrays.copyOfRange(actualResponse, headerEnd + 4, actualResponse.length);

        assertThat(headers).startsWith("HTTP/1.1 200 OK")
                .contains("Content-Type: image/png \r\n")
                .endsWith("Content-Length: " + expectedBody.length + " ");
        assertThat(actualBody).isEqualTo(expectedBody);
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
