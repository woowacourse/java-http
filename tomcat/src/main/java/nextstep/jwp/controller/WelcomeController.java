package nextstep.jwp.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;

public class WelcomeController extends AbstractController {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @Override
    void doGet(final HttpRequest request, final HttpResponse response) {
        response.setBody(WELCOME_MESSAGE);
        response.setStatus(HttpStatus.OK);
    }
}
