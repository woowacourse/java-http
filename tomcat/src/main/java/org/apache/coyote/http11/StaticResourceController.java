package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        sendFile(request.requestLine().path(), response);
    }

    void sendFile(String path, HttpResponse response) throws IOException {
        String resourcePath = "static" + path;
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("정적 파일을 찾을 수 없습니다: " + resourcePath);
            }
            String body = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
            response.send(contentType(path), body);
        }
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
