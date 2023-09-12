package nextstep.jwp.controller;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;

public class HelloWorldController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) {
        response.setStatus(HttpStatus.OK)
                .setContentType("html")
                .setResponseBody("Hello world!");
    }
}
