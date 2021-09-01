package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.entity.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public String doService(HttpRequest httpRequest) throws IOException {
        HttpMethod method = httpRequest.method();

        if (method.isSame("GET")) {
            return doGet(httpRequest);
        }
        if (method.isSame("POST")) {
            return doPost(httpRequest);
        }
        throw new MethodNotAllowedException();
    }

    protected abstract String doGet(HttpRequest httpRequest) throws IOException;

    protected abstract String doPost(HttpRequest httpRequest);
}
