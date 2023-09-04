package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Set;

public abstract class Controller {

    public HttpResponse handleRequest(final HttpRequest httpRequest) {
        final String method = httpRequest.getMethod();
        if (checkMethod(method)) {
            return handle(httpRequest);
        }
        return HttpResponse.init();
    }

    private boolean checkMethod(final String method) {
        final Set<String> methods = Set.of("GET", "POST");
        return methods.contains(method);
    }

    private HttpResponse handle(final HttpRequest httpRequest) {
        final String method = httpRequest.getMethod();
        if ("GET".equals(method)) {
            return doGet(httpRequest);
        }
        if ("POST".equals(method)) {
            return doPost(httpRequest);
        }
        if ("PATCH".equals(method)) {

        }
        if ("DELETE".equals(method)) {

        }
        return HttpResponse.init();
    }

    public abstract HttpResponse doGet(final HttpRequest httpRequest);
    public abstract HttpResponse doPost(final HttpRequest httpRequest);
}
