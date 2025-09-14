package com.techcourse.presentation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);
    private static final String STATIC_DIRECTORY_NAME = "static";

    private final Map<String, Path> staticResources = new ConcurrentHashMap<>();

    protected AbstractController() {
        loadStaticResources();
    }

    private void loadStaticResources() {
        final URL baseUrl = Thread.currentThread()
                .getContextClassLoader()
                .getResource(STATIC_DIRECTORY_NAME);

        if (baseUrl != null) {
            final Path basePath = getStaticBasePath(baseUrl);
            registerResourcePaths(basePath);
        }
    }

    private Path getStaticBasePath(final URL baseUrl) {
        final Path basePath;
        try {
            basePath = Paths.get(baseUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        log.debug("정적 자원 디렉토리 경로: {}", basePath);
        return basePath;
    }

    private void registerResourcePaths(final Path basePath) {
        try (final Stream<Path> files = Files.walk(basePath)) {
            files.filter(Files::isRegularFile)
                    .forEach(path -> {
                        final String pathName = "/" + basePath.relativize(path);
                        staticResources.computeIfAbsent(pathName, key -> path);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug("정적 자원 경로 등록 완료: {}", staticResources.keySet());
    }

    protected abstract String getBasePath();

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

    protected boolean isStaticResource(final String path) {
        if ("/".equals(path)) {
            return true;
        }
        return staticResources.containsKey(path);
    }

    protected HttpResponse renderStaticPage(final String path, final String protocol) {
        if (!isStaticResource(path)) {
            throw new IllegalArgumentException("요청 경로에 해당하는 자원이 없습니다.");
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
        final Path filePath = staticResources.get(path);
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
