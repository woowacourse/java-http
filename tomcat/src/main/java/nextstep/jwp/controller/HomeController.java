package nextstep.jwp.controller;

import nextstep.jwp.exception.HttpRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

public class HomeController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new HttpRequestException(HTTP_METHOD_EXCEPTION_MESSAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final var statusLine = StatusLine.of(request.getRequestLine().getProtocol(), HttpStatus.OK);
        final var responseBody = ResponseBody.fromText("Hello world!");
        response.setStatusLine(statusLine);
        response.addResponseHeader("Content-Type", TEXT_HTML);
        response.addResponseHeader("Content-Length", String.valueOf(responseBody.getBody().getBytes().length));
        response.setResponseBody(responseBody);
    }
}
