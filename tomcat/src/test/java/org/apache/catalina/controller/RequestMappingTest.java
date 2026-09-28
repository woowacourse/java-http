package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    @Test
    void routesByPathWithoutQueryString() throws Exception {
        assertThat(service("/custom?name=java")).endsWith("\r\n\r\ncustom response");
    }

    @Test
    void usesDefaultControllerForUnmappedPath() throws Exception {
        assertThat(service("/unknown")).endsWith("\r\n\r\ndefault response");
    }

    private String service(final String path) throws Exception {
        final Controller custom = (request, response) -> response.body("custom response");
        final Controller fallback = (request, response) -> response.body("default response");
        final var mapping = new RequestMapping(Map.of("/custom", custom), fallback);
        final var request = HttpRequest.read(new ByteArrayInputStream(
                ("GET " + path + " HTTP/1.1\r\n\r\n").getBytes(StandardCharsets.UTF_8)));
        final var output = new ByteArrayOutputStream();
        final var response = new HttpResponse(output);

        mapping.getController(request).service(request, response);
        response.flush();
        return output.toString(StandardCharsets.UTF_8);
    }
}
