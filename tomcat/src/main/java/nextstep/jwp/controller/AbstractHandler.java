package nextstep.jwp.controller;

import org.apache.coyote.exception.UncheckedServletException;
import org.apache.coyote.model.request.HttpRequest;

public abstract class AbstractHandler implements Handler {

    protected final HttpRequest httpRequest;

    public AbstractHandler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        if (httpRequest.checkGetMethod()) {
            return getMethodResponse();
        }
        if (httpRequest.checkPostMethod()) {
            return postMethodResponse();
        }
        return otherMethodResponse();
    }

    protected String getMethodResponse() {
        throw new UncheckedServletException("지원하지 않습니다.");
    }

    protected String postMethodResponse() {
        throw new UncheckedServletException("지원하지 않습니다.");
    }

    protected String otherMethodResponse() {
        throw new UncheckedServletException("지원하지 않습니다.");
    }
}
