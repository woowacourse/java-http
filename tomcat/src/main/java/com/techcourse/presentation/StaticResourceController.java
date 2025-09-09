package com.techcourse.presentation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);
    private static final String BASE_DIRECTORY_NAME = "static";
    private static final Map<String, Path> RESOURCE_PATHS = new ConcurrentHashMap<>();

    public StaticResourceController() {
        final URL baseUrl = Thread.currentThread()
                .getContextClassLoader()
                .getResource(BASE_DIRECTORY_NAME);

        if (baseUrl != null) {
            final Path basePath = getBasePath(baseUrl);
            registerResourcePaths(basePath);
        }
    }

    private Path getBasePath(final URL baseUrl) {
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
                        RESOURCE_PATHS.computeIfAbsent(pathName, key -> path);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug("정적 자원 경로 등록 완료: {}", RESOURCE_PATHS.keySet());
    }

    @Override
    public boolean isResponsible(final String path) {
        if ("/".equals(path)) {
            return true;
        }
        return RESOURCE_PATHS.containsKey(path);
    }

    @Override
    public HttpResponse getResource(final HttpRequest request) {
        if (!isResponsible(request.path())) {
            throw new IllegalArgumentException("요청 경로에 해당하는 자원이 없습니다.");
        }

        final Map<String, String> responseHeaders = new LinkedHashMap<>();

        if ("/".equals(request.path())) {
            final String body = "Hello world!";

            responseHeaders.put("Content-Type", "text/html;charset=utf-8");
            responseHeaders.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

            return new HttpResponse(request.protocol(), "200 OK", responseHeaders, body);
        }

        final Path filePath = RESOURCE_PATHS.get(request.path());
        final String statusCode = getStatusCode(request.path());

        try {
            final String contentType = Files.probeContentType(filePath);
            final String body = new String(Files.readAllBytes(filePath));

            responseHeaders.put("Content-Type", contentType + ";charset=utf-8");
            responseHeaders.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

            return new HttpResponse(request.protocol(), statusCode, responseHeaders, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
