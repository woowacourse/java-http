package org.apache.coyote.http11.controller;

import java.util.Set;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public final class StaticResourceController extends AbstractController {

    private static final Set<HttpMethod> ALLOWED_METHODS = Set.of(HttpMethod.GET);

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        if (!isStaticResource(request.getPath())) {
            return respondNotFound(request);
        }
        return super.service(request);
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".html") || path.endsWith(".css")
                || path.endsWith(".js") || path.endsWith(".svg");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return respondWithStaticResource(request, request.getPath());
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
