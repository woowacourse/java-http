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
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new StringResponseTemplate().ok(RESPONSE);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        return super.doPost(httpRequest);
    }
}
