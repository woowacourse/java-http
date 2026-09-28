package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void returnsMappedController() throws Exception {
        HttpRequest request = request("GET /login HTTP/1.1");

        assertThat(requestMapping.getController(request)).isInstanceOf(LoginController.class);
    }

    @Test
    void returnsStaticResourceControllerForUnmappedPath() throws Exception {
        HttpRequest request = request("GET /401.html HTTP/1.1");

        assertThat(requestMapping.getController(request)).isInstanceOf(StaticResourceController.class);
    }

    private HttpRequest request(String requestLine) throws Exception {
        String rawRequest = requestLine + "\r\nHost: localhost:8080\r\n\r\n";
        return HttpRequest.from(new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));
    }
}
