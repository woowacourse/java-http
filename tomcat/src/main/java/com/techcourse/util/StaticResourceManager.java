package com.techcourse.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StaticResourceManager {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceManager.class);
    private static final String STATIC_DIRECTORY_NAME = "static";
    private static final Map<String, Path> STATIC_RESOURCES = new ConcurrentHashMap<>();

    private StaticResourceManager() {
    }

    public static void initialize() {
        loadStaticResources();
    }

    private static void loadStaticResources() {
        final URL baseUrl = Thread.currentThread()
                .getContextClassLoader()
                .getResource(STATIC_DIRECTORY_NAME);

        if (baseUrl != null) {
            final Path basePath = getStaticBasePath(baseUrl);
            registerResourcePaths(basePath);
        }
    }

    private static Path getStaticBasePath(final URL baseUrl) {
        final Path basePath;
        try {
            basePath = Paths.get(baseUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        log.info("정적 자원 디렉토리 경로: {}", basePath);
        return basePath;
    }

    private static void registerResourcePaths(final Path basePath) {
        try (final Stream<Path> files = Files.walk(basePath)) {
            files.filter(Files::isRegularFile)
                    .forEach(path -> {
                        final String pathName = "/" + basePath.relativize(path);
                        STATIC_RESOURCES.computeIfAbsent(pathName, key -> path);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("정적 자원 경로 등록 완료: {}", STATIC_RESOURCES.keySet());
    }

    public static boolean isStaticResource(final String path) {
        if ("/".equals(path)) {
            return true;
        }
        return STATIC_RESOURCES.containsKey(path);
    }

    public static Path getResourcePath(final String path) {
        return STATIC_RESOURCES.get(path);
    }

    public static ResourceWithType getResource(final String path) {
        if ("/".equals(path)) {
            return new ResourceWithType("Hello world!", "text/plain");
        }

        final Path filePath = STATIC_RESOURCES.get(path);

        try {
            final String contentType = Files.probeContentType(filePath);
            final String content = new String(Files.readAllBytes(filePath));
            return new ResourceWithType(content, contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
