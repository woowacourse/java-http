package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

public class HomeController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setStatusLine(statusLine);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK);
        final var responseBody = ResponseBody.fromText("Hello world!");
        httpResponse.setStatusLine(statusLine);
        httpResponse.addResponseHeader("Content-Type", TEXT_HTML);
        httpResponse.addResponseHeader("Content-Length", String.valueOf(responseBody.getBody().getBytes().length));
        httpResponse.setResponseBody(responseBody);
    }
}
