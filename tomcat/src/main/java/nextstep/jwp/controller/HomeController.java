package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import static org.apache.coyote.http11.request.ContentType.HTML;


public class HomeController extends AbstractController {
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        StatusLine statusLine = new StatusLine(request.getProtocolVersion(), HttpStatus.OK);
        response
                .statusLine(statusLine)
                .contentType(HTML.getValue())
                .contentLength(DEFAULT_MESSAGE.getBytes().length)
                .responseBody(new ResponseBody(DEFAULT_MESSAGE));
    }

}
