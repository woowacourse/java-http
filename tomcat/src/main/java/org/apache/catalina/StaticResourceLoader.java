package org.apache.catalina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class StaticResourceLoader {

    private static final String ROOT = "static/";

    private StaticResourceLoader() {
    }

    public static String read(final String path) throws IOException {
        final String resourceName = ROOT + normalize(path);
        try (InputStream inputStream = StaticResourceLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("리소스를 찾을 수 없음: " + resourceName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String normalize(final String path) {
        final String resourcePath = Objects.requireNonNull(path, "리소스 경로는 null일 수 없습니다.");
        final String normalized = resourcePath.startsWith("/")
                ? resourcePath.substring(1)
                : resourcePath;

        if (normalized.contains("..")) {
            throw new IllegalArgumentException("상위 경로 접근은 허용하지 않습니다: " + path);
        }
        return normalized;
    }
}
