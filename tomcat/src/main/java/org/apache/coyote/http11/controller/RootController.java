package org.apache.coyote.http11.controller;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public final class RootController extends AbstractController {

    private static final Set<HttpMethod> ALLOWED_METHODS = Set.of(HttpMethod.GET);

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        return HttpResponse.ok(request.getVersion(), "text/html;charset=utf-8", body);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        return respondMethodNotAllowed(request);
    }

    @Override
    protected Set<HttpMethod> allowedMethods() {
        return ALLOWED_METHODS;
    }
}
