package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.handler.HandlerResult.DEFAULT_CONTENT_TYPE;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.resolver.PathResolver;

public class StaticFileHandler implements Handler {

    private static final String STATIC_ROOT = "static/";
    private static final String NOT_FOUND_PAGE = "404.html";

    @Override
    public HandlerResult doHandle(final HttpRequest request) {
        final String resolved = PathResolver.resolve(request.route());
        final String resourcePath = STATIC_ROOT + resolved;

        try {
            // 기존 리소스 요청
            final HandlerResult served = tryServe(resourcePath);
            if (served != null) {
                return served;
            }

            // Fallback: 404 Page
            final String notFoundResourcePath = STATIC_ROOT + NOT_FOUND_PAGE;
            final HandlerResult notFound = tryServe(notFoundResourcePath);
            if (notFound != null) {
                return notFound;
            } else {
                return HandlerResult.notFound("404 Not Found");
            }

        } catch (final IOException e) {
            return HandlerResult.serverError(
                    String.format("Failed to read static resource: %s (%s)", resourcePath, e.getMessage())
            );
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
            contentType = DEFAULT_CONTENT_TYPE;
        }

        // 4. 파일 내용 읽기
        final byte[] body = Files.readAllBytes(filePath);

        return HandlerResult.ok(contentType, body);
    }
}
