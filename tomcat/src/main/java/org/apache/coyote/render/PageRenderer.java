package org.apache.coyote.render;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.dto.HttpHeader;
import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.dto.HttpResponse;
import org.apache.coyote.handler.AbstractController;
import org.apache.coyote.util.HttpResponseBuilder;

public class PageRenderer extends AbstractController {

    private static final String STATIC_FILE_ROOT = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        createStaticFileResponse(request.version(), HttpStatus.OK.getStatusCode(), request.path(), response);
    }

    public static void createStaticFileResponse(String version, int statusCode, String path, HttpResponse response) {
        try {
            path = PageEndpoint.findPageByPath(path.trim());
            String content = readStaticFile(path);
            String contentType = ContentType.findContentType(path);
            HttpHeader httpHeader = new HttpHeader(
                    Map.of(
                            "Content-Type", contentType + ";charset=utf-8",
                            "Content-Length", String.valueOf(content.getBytes().length)
                    )
            );
            HttpResponseBuilder.initResponse(
                    version,
                    statusCode,
                    httpHeader,
                    content,
                    response
            );
        } catch (IOException e) {
            createErrorResponse(version, response);
        }
    }

    private static String readStaticFile(final String path) throws IOException {
        String fullPath = STATIC_FILE_ROOT + path;

        try (InputStream inputStream = PageRenderer.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                throw new IOException("파일을 찾을 수 없습니다: " + fullPath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void createErrorResponse(String version, HttpResponse response) {
        try {
            String content = readStaticFile("/404.html");
            String contentType = ContentType.HTML.getContentType();
            HttpHeader httpHeader = new HttpHeader(
                    Map.of(
                            "Content-Type", contentType + ";charset=utf-8",
                            "Content-Length", String.valueOf(content.getBytes().length)
                    )
            );

            HttpResponseBuilder.initResponse(
                    version,
                    404,
                    httpHeader,
                    content,
                    response
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("페이지 처리중 에러가 발생했습니다.");
        }
    }

    public static void sendRedirect(String version, int statusCode, HttpHeader headers, HttpResponse response) {
        HttpResponseBuilder.initResponse(version, statusCode, headers, "", response);
    }
}
