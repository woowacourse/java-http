package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import java.io.IOException;

public class ResourceController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.METHOD_NOT_ALLOWED);
        response.setStatusLine(statusLine);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.OK);
        final String uri = request.getRequestLine().getPath().split("\\?")[0];
        final var responseBody = ResponseBody.fromUri(uri);
        response.setStatusLine(statusLine);
        response.addResponseHeader("Content-Type", getContentType(uri));
        response.addResponseHeader("Content-Length", String.valueOf(responseBody.getBody().getBytes().length));
        response.setResponseBody(responseBody);
    }

    private String getContentType(final String uri) {
        if (uri.endsWith(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
