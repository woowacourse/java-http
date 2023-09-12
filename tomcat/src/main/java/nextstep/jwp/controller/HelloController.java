package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.OK;
import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.header.HttpHeaders.CONTENT_TYPE;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.handler.AbstractController;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String message = "Hello world!";

        response.setVersion(request.protocolVersion());
        response.setStatus(OK);
        response.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
        response.addHeader(CONTENT_LENGTH, String.valueOf(message.length()));
        response.setBody(message);
    }
}
