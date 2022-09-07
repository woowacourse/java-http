package nextstep.jwp.handler;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class RootRequestHandler extends AbstractHttpRequestHandler {

    private final HttpVersion httpVersion;

    public RootRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        String responseBody = "Hello world!";
        return HttpResponse.ok(httpVersion, HttpCookie.empty(), responseBody);
    }
}
