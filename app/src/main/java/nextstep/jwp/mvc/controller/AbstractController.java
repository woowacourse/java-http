package nextstep.jwp.mvc.controller;

import java.io.IOException;
import nextstep.jwp.http.common.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        HttpMethod method = request.getMethod();
        if (method.isSame("GET")) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected abstract HttpResponse doGet(HttpRequest request) throws IOException;

    protected abstract HttpResponse doPost(HttpRequest request) throws IOException;
}
