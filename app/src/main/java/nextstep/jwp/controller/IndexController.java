package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class IndexController extends Controller {
    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return "GET".equals(httpMethod) && "/index.html".equals(path);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
