package nextstep.jwp.controller;

import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.response.StatusCode;

public class HomeController extends AbstractController {

    @Override
    public void doGet(final Request request, final Response response) {
        response.setHttpResponseStartLine(StatusCode.OK);
        byte[] body = "Hello world!".getBytes();
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        response.setResponseBody(body);
    }
}
