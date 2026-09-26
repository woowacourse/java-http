package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class StaticResourceController extends AbstractController {

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css;charset=utf-8",
            "js", "text/javascript;charset=utf-8",
            "svg", "image/svg+xml;charset=utf-8"
    );

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        serve(request.getPath(), response);
    }

    public static void serve(String path, HttpResponse response) throws IOException {
        String decodedPath = decodePath(path);
        String contentType = contentType(decodedPath);
        if (contentType == null || decodedPath.endsWith("/")) {
            response.sendError(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.");
            return;
        }

        try (InputStream resource = StaticResourceController.class.getClassLoader()
                .getResourceAsStream("static" + decodedPath)) {
            if (resource == null) {
                response.sendError(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.");
                return;
            }
            response.setContentType(contentType);
            response.setBody(resource.readAllBytes());
        }
    }

    private static String decodePath(String path) {
        URI uri = URI.create(path);
        String decodedPath = uri.getPath();
        if (uri.isAbsolute() || uri.getRawAuthority() != null || decodedPath == null
                || !decodedPath.startsWith("/") || decodedPath.indexOf('\\') >= 0
                || decodedPath.indexOf('\0') >= 0
                || Arrays.asList(decodedPath.split("/")).contains("..")) {
            throw new IllegalArgumentException("정적 리소스 디렉터리를 벗어날 수 없습니다.");
        }
        return decodedPath;
    }

    private static String contentType(String path) {
        String extension = path.substring(path.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return CONTENT_TYPES.get(extension);
    }
}
