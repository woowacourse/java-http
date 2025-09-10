package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpConstants.NOT_FOUND_PAGE;
import static org.apache.coyote.http11.HttpConstants.SERVER_ERROR_PAGE;
import static org.apache.coyote.http11.HttpConstants.SLASH;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.resolver.PathResolver;

public class StaticFileHandler implements Handler {

    private static final String STATIC_ROOT = "static";

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

        // 3. ContentType 판별
        final ContentType contentType = ContentType.fromPath(resourcePath);

        // 4. 파일 내용 읽기
        final byte[] body = Files.readAllBytes(filePath);

        return HandlerResult.builder()
                .status(Status.OK)
                .contentType(contentType)
                .body(body)
                .build();
    }

    // 404 Not Found 처리
    private HandlerResult handleNotFound() {
        try {
            final String resourcePath = STATIC_ROOT + SLASH + NOT_FOUND_PAGE;
            final HandlerResult result = tryServe(resourcePath);
            if (result != null) {
                return HandlerResult.builder()
                        .status(Status.NOT_FOUND)
                        .contentType(ContentType.HTML)
                        .body(result.body())
                        .build();
            }
        } catch (final IOException ignored) {
        }

        return HandlerResult.builder()
                .status(Status.NOT_FOUND)
                .contentType(ContentType.TEXT)
                .body(Status.NOT_FOUND.line().getBytes(StandardCharsets.UTF_8))
                .build();
    }

    // 500 Server Error 처리
    private HandlerResult handleServerError() {
        try {
            final String resourcePath = STATIC_ROOT + SLASH + SERVER_ERROR_PAGE;
            final HandlerResult result = tryServe(resourcePath);
            if (result != null) {
                return HandlerResult.builder()
                        .status(Status.INTERNAL_ERROR)
                        .contentType(ContentType.HTML)
                        .body(result.body())
                        .build();
            }
        } catch (final IOException ignored) {
        }

        return HandlerResult.builder()
                .status(Status.INTERNAL_ERROR)
                .contentType(ContentType.TEXT)
                .body(Status.INTERNAL_ERROR.line().getBytes(StandardCharsets.UTF_8))
                .build();
    }
}
