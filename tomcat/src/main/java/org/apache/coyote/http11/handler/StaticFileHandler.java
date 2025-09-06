package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class StaticFileHandler implements HttpRequestHandler {

    private static final String STATIC_DIR = "static";

    @Override
    public boolean canHandle(HttpRequest request) {
        String path = request.getRequestPath();
        return path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js");
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String path = request.getRequestPath();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STATIC_DIR + path)) {

            validateInputStream(inputStream);

            byte[] content = inputStream.readAllBytes();

            HttpHeaders headers = HttpHeaders.fromLines(
                    List.of(
                            "Content-Type: " + getContentType(path),
                            "Content-Length: " + content.length
                    )
            );

            return new HttpResponse(HttpStatus.OK, headers, HttpBody.from(content));
        }
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new NotFoundException();
        }
    }

    //TODO: enum으로 분리  (2025-09-4, 목, 7:1)
    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (path.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream"; // 기본 바이너리
    }
}
