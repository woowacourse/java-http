package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.apache.coyote.http11.util.PathParser;

public class ResourceRequestHandler implements RequestHandler {
    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        final var requestUri = request.getRequestUri();
        final var rowRequestUri = requestUri.getRequestUri();
        PathParser.checkPath(rowRequestUri);
        response.setStatusCode(HttpStatus.OK);
        return rowRequestUri;
    }

    @Override
    public boolean support(final HttpRequest request) {
        return true;
    }
}
