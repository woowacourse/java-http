package nextstep.jwp.handler;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.util.ResourcesUtil;

public class StaticRequestHandler extends AbstractHttpRequestHandler {

    private final HttpVersion httpVersion;

    public StaticRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return HttpResponse.ok(httpVersion, HttpCookie.empty(), httpRequest.getContentType(), responseBody);
    }
}
