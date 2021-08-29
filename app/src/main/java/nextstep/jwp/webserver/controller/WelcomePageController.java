package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.template.StringResponseTemplate;

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
