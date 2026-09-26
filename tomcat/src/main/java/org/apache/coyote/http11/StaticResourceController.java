package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final byte[] body = load(request.getPath());
        response.setHeader("Content-Type", contentType(request.getPath()));
        response.setBody(body);
        SessionSupport.addCookie(response, SessionSupport.from(request));
    }

    static byte[] load(final String path) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        final String resourcePath = switch (path) {
            case "/login" -> "/login.html";
            case "/register" -> "/register.html";
            default -> path;
        };
        try (InputStream resource = StaticResourceController.class.getClassLoader()
                .getResourceAsStream("static" + resourcePath)) {
            if (resource == null) {
                throw new IllegalArgumentException("리소스를 찾을 수 없습니다: static" + resourcePath);
            }
            return resource.readAllBytes();
        }
    }

    private static String contentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
