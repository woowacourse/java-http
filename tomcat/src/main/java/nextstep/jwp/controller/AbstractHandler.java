package nextstep.jwp.controller;

import org.apache.coyote.exception.UncheckedServletException;
import org.apache.coyote.model.request.HttpRequest;

public abstract class AbstractHandler implements Handler {

    public AbstractHandler() {
    }

    @Override
    public String getResponse(final HttpRequest httpRequest) {
        if (httpRequest.checkGetMethod()) {
            return getMethodResponse(httpRequest);
        }
        if (httpRequest.checkPostMethod()) {
            return postMethodResponse(httpRequest);
        }
        return otherMethodResponse(httpRequest);
    }

    protected String getMethodResponse(final HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않습니다.");
    }

    protected String postMethodResponse(final HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않습니다.");
    }

    protected String otherMethodResponse(final HttpRequest httpRequest) {
        throw new UncheckedServletException("지원하지 않습니다.");
    }
}
