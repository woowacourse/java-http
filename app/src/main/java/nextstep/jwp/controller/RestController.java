package nextstep.jwp.controller;

import static nextstep.jwp.http.request.Method.GET;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class RestController implements Controller {

    public HttpResponse service(HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasMethod(GET)) {
            return doGet(httpRequest);
        }

        return doPost(httpRequest);
    }

    protected abstract HttpResponse doGet(HttpRequest httpRequest) throws IOException;

    protected abstract HttpResponse doPost(HttpRequest httpRequest) throws IOException;
}
