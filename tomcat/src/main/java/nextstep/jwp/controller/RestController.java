package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;

public abstract class RestController implements Controller {

    protected final String path;

    public RestController(final String path) {
        this.path = path;
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final HttpRequestURI httpRequestURI = httpRequest.getRequestURI();
        return httpRequestURI.hasSamePath(path);
    }
}
