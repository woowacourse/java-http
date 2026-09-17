package org.apache.coyote.http11;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

public class StaticResourceLoader {

    private static final Path STATIC_ROOT = Path.of("static");

    private final ClassLoader classLoader;

    public StaticResourceLoader() {
        this(StaticResourceLoader.class.getClassLoader());
    }

    StaticResourceLoader(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
    }

    public StaticResource load(String path) throws IOException {
        if ("/".equals(path)) {
            return new StaticResource("Hello world!", "text/html");
        }

        String resourcePath = getResourcePath(path);
        String body = readBody(resourcePath, path);

        return new StaticResource(body, getContentType(path));
    }

    private String getResourcePath(String requestPath) {
        String decodedPath = decodePath(requestPath);

        try {
            Path relativePath = Path.of(decodedPath.substring(1));
            Path resourcePath = STATIC_ROOT.resolve(relativePath).normalize();

            if (!resourcePath.startsWith(STATIC_ROOT)) {
                throw new BadRequestException("정적 리소스 루트를 벗어난 경로입니다: " + requestPath);
            }

            return resourcePath.toString().replace('\\', '/');
        } catch (InvalidPathException e) {
            throw new BadRequestException("잘못된 정적 리소스 경로입니다: " + requestPath, e);
        }
    }

    private String decodePath(String requestPath) {
        URI requestUri;

        try {
            requestUri = URI.create(requestPath);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("잘못된 정적 리소스 경로입니다: " + requestPath, e);
        }

        String decodedPath = requestUri.getPath();

        if (requestUri.isAbsolute()
                || requestUri.getRawAuthority() != null
                || requestUri.getRawQuery() != null
                || requestUri.getRawFragment() != null
                || decodedPath == null
                || !decodedPath.startsWith("/")) {
            throw new BadRequestException("잘못된 정적 리소스 경로입니다: " + requestPath);
        }

        return decodedPath;
    }

    private String readBody(String resourcePath, String requestPath) throws IOException {
        InputStream inputStream = findInputStream(resourcePath, requestPath);

        try (inputStream) {
            byte[] bodyBytes = inputStream.readAllBytes();
            return new String(bodyBytes, StandardCharsets.UTF_8);
        }
    }

    private InputStream findInputStream(String resourcePath, String requestPath) throws FileNotFoundException {
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            inputStream = classLoader.getResourceAsStream(resourcePath + ".html");
        }

        if (inputStream == null) {
            throw new FileNotFoundException(requestPath);
        }

        return inputStream;
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html";
    }
}
