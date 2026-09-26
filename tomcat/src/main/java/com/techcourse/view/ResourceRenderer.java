package com.techcourse.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.coyote.http11.HttpResponse;

public class ResourceRenderer {

    public void render(String path, HttpResponse response) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            byte[] body = Objects.requireNonNull(resource).readAllBytes();
            response.setBody(body, resolveContentType(path));
        }
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
