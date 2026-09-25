package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http.response.EmptyBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        return switch (request.method()) {
            case GET -> doGet(request);
            case POST -> doPost(request);
            default -> methodNotAllowed();
        };
    }

    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return methodNotAllowed();
    }

    protected HttpResponse doPost(HttpRequest request) throws IOException {
        return methodNotAllowed();
    }

    private static HttpResponse methodNotAllowed() {
        return HttpResponse.of(HttpStatus.METHOD_NOT_ALLOWED, EmptyBody.INSTANCE);
    }
}
