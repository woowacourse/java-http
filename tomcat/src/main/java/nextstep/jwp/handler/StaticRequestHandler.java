package nextstep.jwp.handler;

import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class StaticRequestHandler extends AbstractHttpRequestHandler {

    public StaticRequestHandler(final HttpVersion httpVersion) {
        super(httpVersion);
    }

    @Override
    protected HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        return handleStaticResourceRequest(httpRequest);
    }
}
