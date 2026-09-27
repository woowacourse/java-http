package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Set;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final Set<HttpMethod> ALLOWED_METHODS = Set.of(HttpMethod.GET, HttpMethod.POST);

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        return switch (request.getMethod()) {
            case GET -> doGet(request);
            case POST -> doPost(request);
            default -> respondMethodNotAllowed(request);
        };
    }

    protected abstract HttpResponse doGet(HttpRequest request) throws Exception;

    protected abstract HttpResponse doPost(HttpRequest request) throws Exception;

    protected HttpResponse respondMethodNotAllowed(HttpRequest request) {
        return HttpResponse.methodNotAllowed(request.getVersion(), allowedMethods());
    }

    protected Set<HttpMethod> allowedMethods() {
        return ALLOWED_METHODS;
    }

    protected HttpResponse respondWithStaticResource(HttpRequest request, String path) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                return respondNotFound(request);
            }
            return HttpResponse.ok(request.getVersion(), resolveContentType(path), inputStream.readAllBytes());
        }
    }

    protected HttpResponse respondNotFound(HttpRequest request) throws IOException {
        return HttpResponse.notFound(request.getVersion(), readResource("static/404.html"));
    }

    protected HttpResponse respondUnauthorized(HttpRequest request) throws IOException {
        return HttpResponse.unauthorized(request.getVersion(), readResource("static/401.html"));
    }

    private byte[] readResource(String path) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException(path + "을 찾을 수 없습니다.");
            }
            return inputStream.readAllBytes();
        }
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }
}
