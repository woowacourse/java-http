package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.StringResponseTemplate;

public class RegisterPageController extends AbstractController {

    private static final String RESPONSE = "Hello world!";

    public RegisterPageController() {
        super("/", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return new StringResponseTemplate().ok(RESPONSE);
    }
}
