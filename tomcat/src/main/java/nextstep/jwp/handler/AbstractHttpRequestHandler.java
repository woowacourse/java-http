package nextstep.jwp.handler;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public abstract class AbstractHttpRequestHandler implements HttpRequestHandler {

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
}
