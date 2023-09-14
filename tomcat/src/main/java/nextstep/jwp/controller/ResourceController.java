package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import java.io.IOException;

public class ResourceController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setStatusLine(statusLine);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK);
        final String uri = httpRequest.getRequestLine().getPath().split("\\?")[0];
        final var responseBody = ResponseBody.fromUri(uri);
        httpResponse.setStatusLine(statusLine);
        httpResponse.addResponseHeader("Content-Type", getContentType(uri));
        httpResponse.addResponseHeader("Content-Length", String.valueOf(responseBody.getBody().getBytes().length));
        httpResponse.setResponseBody(responseBody);
    }

    private String getContentType(final String uri) {
        if (uri.endsWith(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
