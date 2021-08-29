package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.*;

public class WelcomePageController extends AbstractController {

    private static final String RESPONSE = "Hello world!";

    public WelcomePageController() {
        super("/", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new StringResponseTemplate().ok(RESPONSE);
    }
}
