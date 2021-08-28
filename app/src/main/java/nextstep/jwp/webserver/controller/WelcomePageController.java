package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;

public class WelcomePageController extends AbstractController {

    public static final String RESPONSE = "Hello world!";

    public WelcomePageController() {
        super("/", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return HttpResponse.ok()
                           .body(RESPONSE)
                           .contentLength(RESPONSE.getBytes().length)
                           .build();
    }
}
