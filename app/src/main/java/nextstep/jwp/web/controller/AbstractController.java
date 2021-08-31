package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;

public abstract class AbstractController implements Controller {
    @Override
    public String doService(HttpRequest httpRequest) throws IOException {
        String method = httpRequest.method();

        if ("GET".equals(method)) {
            return doGet(httpRequest);
        }
        if ("POST".equals(method)) {
            return doPost(httpRequest);
        }
        throw new MethodNotAllowedException();
    }

    protected abstract String doGet(HttpRequest httpRequest) throws IOException;

    protected abstract String doPost(HttpRequest httpRequest);
}
