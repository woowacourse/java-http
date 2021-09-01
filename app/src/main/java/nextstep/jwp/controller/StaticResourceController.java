package nextstep.jwp.controller;

import nextstep.jwp.http.*;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

import java.util.UUID;

public class StaticResourceController extends AbstractController {
    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();
        return httpMethod.isGet() && isStaticFileRequest(path);
    }

    private boolean isStaticFileRequest(String path) {
        return path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".ico");
    }

    @Override
    protected UUID createUuid(final HttpRequest httpRequest) {
        return null;
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
