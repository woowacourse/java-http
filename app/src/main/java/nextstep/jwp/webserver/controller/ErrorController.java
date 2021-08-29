package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;

public class ErrorController extends AbstractController {

    public static final ErrorController INSTANCE = new ErrorController();

    public ErrorController() {
        super("/error", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new ResourceResponseTemplate().ok("/404.html");
    }
}
