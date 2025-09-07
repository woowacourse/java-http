package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.handler.HandlerResult.DEFAULT_MIME_TYPE;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.resolver.PathResolver;

public class StaticFileHandler implements Handler {

    private static final String SLASH = "/";
    private static final String STATIC_ROOT = "static";
    private static final String NOT_FOUND_PAGE = "404.html";
    private static final String SERVER_ERROR_PAGE = "500.html";

    @Override
    public HandlerResult doHandle(final HttpRequest request) {
        final String resolved = PathResolver.resolve(request.route());
        final String resourcePath = STATIC_ROOT + SLASH + resolved;

        try {
            // 기존 리소스 요청
            final HandlerResult served = tryServe(resourcePath);
            if (served != null) {
                return served;
            }

            return handleNotFound();    // 404 Page
        } catch (final IOException e) {
            return handleServerError(); // 500 Page
        }
    }

    private HandlerResult tryServe(final String resourcePath) throws IOException {
        // 1. 클래스패스에서 리소스 탐색
        final URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }

        // 2. URL → Path 변환 후 파일 존재 여부 확인
        final Path filePath = Paths.get(resourceUrl.getPath());
        if (!Files.exists(filePath)) {
            return null;
        }

        // 3. MIME 타입 판별
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = DEFAULT_MIME_TYPE;
        }

        // 4. 파일 내용 읽기
        final byte[] body = Files.readAllBytes(filePath);

        return HandlerResult.ok(contentType, body);
    }

    // 404 Not Found 처리
    private HandlerResult handleNotFound() {
        try {
            final String resourcePath = STATIC_ROOT + SLASH + NOT_FOUND_PAGE;
            final HandlerResult result = tryServe(resourcePath);
            if (result != null) {
                return HandlerResult.notFound(result.mimeType(), result.body());
            }
        } catch (final IOException ignored) {
        }
        return HandlerResult.notFound("404 Not Found");
    }

    // 500 Server Error 처리
    private HandlerResult handleServerError() {
        try {
            final String resourcePath = STATIC_ROOT + SLASH + SERVER_ERROR_PAGE;
            final HandlerResult result = tryServe(resourcePath);
            if (result != null) {
                return HandlerResult.serverError(result.mimeType(), result.body());
            }
        } catch (final IOException ignored) {
        }
        return HandlerResult.serverError("500 Internal Server Error");
    }
}
