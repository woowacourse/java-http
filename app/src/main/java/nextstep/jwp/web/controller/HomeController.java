package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.ViewResolver;

public class HomeController extends AbstractController {

    @Override
    protected String doGet(HttpRequest httpRequest) throws IOException {
        return ViewResolver.resolveView("index");
    }

    @Override
    protected String doPost(HttpRequest httpRequest) {
        throw new MethodNotAllowedException();
    }
}
