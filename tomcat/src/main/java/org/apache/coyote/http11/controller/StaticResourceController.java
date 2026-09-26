package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setBody(readResource(request.getPath()), resolveContentType(request.getPath()));
    }

    private byte[] readResource(final String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (resource == null) {
                throw new IOException("정적 리소스를 찾을 수 없습니다: " + path);
            }
            return resource.readAllBytes();
        }
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
