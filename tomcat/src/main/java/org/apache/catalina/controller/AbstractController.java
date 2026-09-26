package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final HttpResponse handle(HttpRequest request) throws IOException {
        return switch (request.requestLine().method()) {
            case "GET" -> doGet(request);
            case "POST" -> doPost(request);
            default -> HttpResponse.empty(405, "Method Not Allowed");
        };
    }

    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponse.empty(405, "Method Not Allowed");
    }

    protected HttpResponse doPost(HttpRequest request) throws IOException {
        return HttpResponse.empty(405, "Method Not Allowed");
    }
}
