package com.techcourse.presentation;

import com.techcourse.util.StaticResourceManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractController implements Controller {

    protected abstract String getBasePath();

    @Override
    public boolean canHandle(final String uri) {
        final String basePath = getBasePath();
        return basePath != null && basePath.equals(uri);
    }

    @Override
    public final HttpResponse service(final HttpRequest request) {
        validateRequest(request);
        final String method = request.requestLine().getMethod();

        if ("GET".equals(method)) {
            return doGet(request);
        }
        if ("POST".equals(method)) {
            return doPost(request);
        }

        throw new IllegalArgumentException("현재는 GET, POST 메서드만 처리 가능합니다.");
    }

    protected void validateRequest(final HttpRequest request) {
        final String uri = request.requestLine().getUri();
        final String basePath = getBasePath();

        if (basePath != null && !basePath.equals(uri)) {
            throw new IllegalArgumentException("요청 경로와 일치하는 API가 존재하지 않습니다.");
        }
    }

    protected HttpResponse doGet(final HttpRequest request) {
        throw new IllegalArgumentException("GET 메서드는 지원되지 않습니다.");
    }

    protected HttpResponse doPost(final HttpRequest request) {
        throw new IllegalArgumentException("POST 메서드는 지원되지 않습니다.");
    }

    protected HttpResponse renderStaticPage(final String path, final String protocol) {
        if (!StaticResourceManager.isStaticResource(path)) {
            throw new IllegalArgumentException("정적 자원이 존재하지 않는 요청 경로: " + path);
        }

        if ("/".equals(path)) {
            return createRootPathResponse(protocol);
        }

        return createFileResponse(path, protocol);
    }

    private HttpResponse createRootPathResponse(final String protocol) {
        final String body = "Hello world!";
        return buildResponse(protocol, "200 OK", "text/html;charset=utf-8", body);
    }

    private HttpResponse createFileResponse(final String path, final String protocol) {
        final Path filePath = StaticResourceManager.getResourcePath(path);
        final String statusCode = getStatusCode(path);

        try {
            final String contentType = Files.probeContentType(filePath);
            final String body = new String(Files.readAllBytes(filePath));
            return buildResponse(protocol, statusCode, contentType + ";charset=utf-8", body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse buildResponse(
            final String protocol,
            final String statusCode,
            final String contentType,
            final String body
    ) {
        return HttpResponse.builder()
                .protocol(protocol)
                .statusCode(statusCode)
                .contentType(contentType)
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }

    private String getStatusCode(final String pathName) {
        return switch (pathName) {
            case "/401.html" -> "401 Unauthorized";
            case "/404.html" -> "404 Not Found";
            case "/500.html" -> "500 Internal Server Error";
            default -> "200 OK";
        };
    }
}
