package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.helper.Responses;
import org.apache.coyote.http11.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler implements Handler {

    private static final String SUFFIX = "/";
    private static final char DOT = '.';
    private static final String STATIC_REGEX = "^/static/?";
    private static final String INVALID_PATH_SEQUENCE = "..";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    private final String base;
    private final String defaultDocument;

    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
            Map.entry("html", "text/html;charset=utf-8"),
            Map.entry("css",  "text/css;charset=utf-8"),
            Map.entry("js",   "application/javascript;charset=utf-8"),
            Map.entry("json", "application/json;charset=utf-8")
    );

    public StaticResourceHandler(String base, String defaultDocument) {
        this.base = base.endsWith(SUFFIX) ? base.substring(0, base.length()-1) : base;
        this.defaultDocument = defaultDocument;
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        String path = request.path().replaceFirst(STATIC_REGEX, "");
        path = normalizePath(path);
        if (path.contains(INVALID_PATH_SEQUENCE)) {
            return false;
        }
        String fullPath = base + SUFFIX + path;
        return getClass().getClassLoader().getResource(fullPath) != null;
    }

    @Override
    public void handle(HttpRequest request, OutputStream outputStream) throws IOException {
        String resourcePath = request.path().replaceFirst(STATIC_REGEX, "");
        String normalizedResourcePath = normalizePath(resourcePath);
        String fullPath = base + SUFFIX + normalizedResourcePath;

        try {
            loadResourceBytes(fullPath).ifPresentOrElse(
                    bytes -> respondBinary(outputStream, request.version(), resourcePath, bytes),
                    () -> respondNotFound(outputStream, request.version())
            );
        } catch (IOException e) {
            log.error("I/O error while serving static resource '{}': {}", request.path(), e.getMessage(), e);
            Responses.serverError(outputStream, request.version());
        } catch (Exception e) {
            log.error("Unexpected error while serving static resource '{}': {}", request.path(), e.getMessage(), e);
            Responses.serverError(outputStream, request.version());
        }
    }

    private String normalizePath(String path) {
        if (path.startsWith(SUFFIX)) {
            path = path.substring(1);
        }
        if (path.isEmpty()) {
            path = defaultDocument;
        }
        return path;
    }

    private Optional<byte[]> loadResourceBytes(String fullPath) throws IOException {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(fullPath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        }
    }

    private void respondBinary(OutputStream out, String version, String resourcePath, byte[] bytes) {
        try {
            String contentType = resolveContentType(resourcePath);
            Responses.binary(out, version,
                    HttpStatus.OK.getCode(), HttpStatus.OK.getReason(),
                    contentType, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void respondNotFound(OutputStream out, String version) {
        try {
            Responses.notFound(out, version);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String resolveContentType(String resourcePath) {
        final String extension = extractExtension(resourcePath);
        return MIME_TYPES.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }

    private String extractExtension(String name) {
        final int lastIndex = name.lastIndexOf(DOT);
        return (lastIndex == -1) ? "" : name.substring(lastIndex + 1).toLowerCase(Locale.ROOT);
    }
}
