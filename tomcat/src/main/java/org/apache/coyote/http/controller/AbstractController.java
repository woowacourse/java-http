package org.apache.coyote.http.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        String method = request.getMethod();

        return switch (method) {
            case "GET" -> doGet(request);
            case "POST" -> doPost(request);
            case "PUT" -> doPut(request);
            case "DELETE" -> doDelete(request);
            default -> throw new UnsupportedOperationException("HTTP method not supported: " + method);
        };
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException("GET method not implemented");
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException("POST method not implemented");
    }

    protected HttpResponse doPut(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException("PUT method not implemented");
    }

    protected HttpResponse doDelete(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException("DELETE method not implemented");
    }

    protected HttpResponse handleStaticFile(String filePath, String contentType) {
        try {
            final var path = Path.of(getClass().getResource("/static" + filePath).getPath());
            final var content = new String(Files.readAllBytes(path));
            return HttpResponse.ok(content, contentType);
        } catch (Exception e) {
            return HttpResponse.ok("404 Not Found", "text/html");
        }
    }
}
