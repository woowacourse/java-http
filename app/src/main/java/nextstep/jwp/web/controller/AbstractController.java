package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.entity.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod method = httpRequest.method();

        if (method.isSame("GET")) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (method.isSame("POST")) {
            doPost(httpRequest, httpResponse);
            return;
        }
        throw new MethodNotAllowedException();
    }

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
