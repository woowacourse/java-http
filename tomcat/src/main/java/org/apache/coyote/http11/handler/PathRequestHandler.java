package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.handler.component.HttpResponse;

public class PathRequestHandler {

    private static Map<String, HttpResponse> mappedRequest = new HashMap<>();

    static {
        mappedRequest.put("/", new HttpResponse(
            "HTTP/1.1 200 OK ",
            "Hello world!",
            List.of(
                "Content-Type: text/html;charset=utf-8 ",
                getContentLengthHeader("Hello World!")
            )
        ));

        try {
            mappedRequest.put("/login", new StaticResourceHandler().getResponse("/login.html"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getContentLengthHeader(final String body) {
        return "Content-Length: " + body.getBytes().length + " ";
    }

    public void add(final String path, final HttpResponse message) {
        if (mappedRequest.containsKey(path)) {
            throw new IllegalArgumentException("이미 맵핑이 된 경로입니다. path : " + path);
        }
        mappedRequest.put(path, message);
    }

    public boolean containsPath(final String path) {
        return mappedRequest.containsKey(path);
    }

    public HttpResponse getResponse(final String path) {
        if (!mappedRequest.containsKey(path)) {
            throw new IllegalArgumentException("맵핑된 경로가 존재하지 않습니다. path : " + path);
        }
        return mappedRequest.get(path);
    }
}
