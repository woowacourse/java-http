package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class HomeController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpResponseStartLine(StatusCode.OK);
        byte[] body = "Hello world!".getBytes();
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        response.setResponseBody(body);
    }
}
