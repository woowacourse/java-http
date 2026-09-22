package org.apache.catalina.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return serve(request.getPath());
    }

    public HttpResponse serve(String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                HttpResponse response = new HttpResponse();
                response.notFound();
                return response;
            }

            String resource = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            HttpResponse response = new HttpResponse();
            response.setBody(resource, getContentType(path));
            return response;
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }
}
