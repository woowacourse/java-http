package nextstep.jwp.handler;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.util.ResourcesUtil;

public abstract class AbstractHttpRequestHandler implements HttpRequestHandler {

    protected final HttpVersion httpVersion;

    public AbstractHttpRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public final HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return handleHttpGetRequest(httpRequest);
        }
        if (httpRequest.isPostMethod()) {
            return handleHttpPostRequest(httpRequest);
        }
        throw new UncheckedServletException("지원하는 method가 존재하지 않습니다.");
    }

    protected HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않는 method입니다.");
    }

    protected HttpResponse handleHttpPostRequest(final HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않는 method입니다.");
    }

    protected final HttpResponse handleStaticResourceRequest(final HttpRequest httpRequest) {
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return HttpResponse.ok(httpVersion, HttpCookie.empty(), httpRequest.getContentType(), responseBody);
    }
}
