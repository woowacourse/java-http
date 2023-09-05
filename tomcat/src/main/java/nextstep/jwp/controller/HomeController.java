package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class HomeController extends HttpServlet {

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) {
        resp.setHttpResponseStartLine(StatusCode.OK);
        byte[] body = "Hello world!".getBytes();
        resp.addHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        resp.setResponseBody(body);
    }
}
