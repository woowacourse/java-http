package org.apache.catalina.controller;

import org.apache.catalina.handler.StaticResourceHandler;
import org.apache.catalina.session.SimpleHttpSession;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class RequestAdapter {

    private static final Logger log = LoggerFactory.getLogger(RequestAdapter.class);

    private final RequestMapping requestMapping;

    public RequestAdapter(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public HttpResponse service(final HttpRequest request) {
        try {
            final var session = request.getSession(true);

            final var controller = requestMapping.getController(request.getPath());
            HttpResponse response = (controller != null)
                    ? controller.service(request)
                    : handleStaticResource(request);

            if (session instanceof SimpleHttpSession s && s.isNew()) {
                response = response.toBuilder()
                        .header("Set-Cookie", "JSESSIONID=" + s.getId())
                        .build();
            }

            return response;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            return HttpResponse.builder()
                    .protocol(request.getProtocol())
                    .status(302, "Found")
                    .header("Location", "/500.html")
                    .contentType("text/html;charset=utf-8")
                    .build();
        }
    }

    private HttpResponse handleStaticResource(final HttpRequest request) throws URISyntaxException, IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return HttpResponse.builder()
                    .protocol(request.getProtocol())
                    .status(405, "Method Not Allowed")
                    .contentType("text/plain;charset=utf-8")
                    .body("Method Not Allowed".getBytes(StandardCharsets.UTF_8))
                    .build();
        }

        return StaticResourceHandler.serveStaticResource(request);
    }
}
