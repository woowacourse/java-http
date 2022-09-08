package nextstep.jwp.controller;

import static org.apache.coyote.request.startline.HttpMethod.*;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.startline.HttpMethod;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        final HttpMethod method = request.getRequestMethod();

        if (method.equals(POST)) {
            return doPost(request);
        }
        return doGet(request);
    }

    protected abstract HttpResponse doPost(HttpRequest request);
    protected abstract HttpResponse doGet(HttpRequest request);
}
