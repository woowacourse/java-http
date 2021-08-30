package nextstep.jwp.controller;

import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        String method = request.getMethod();
        if (method.equals("GET")) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected abstract HttpResponse doGet(HttpRequest request) throws IOException;

    protected abstract HttpResponse doPost(HttpRequest request) throws IOException;
}
