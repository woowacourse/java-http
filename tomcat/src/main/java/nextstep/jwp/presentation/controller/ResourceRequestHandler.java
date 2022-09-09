package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.PathParser;

public class ResourceRequestHandler implements RequestHandler {
    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        final var requestURI = request.getRequestURI();
        final var rowRequestURI = requestURI.getRequestURI();
        PathParser.checkPath(rowRequestURI);
        return rowRequestURI;
    }

    @Override
    public boolean support(final HttpRequest request) {
        return true;
    }
}
