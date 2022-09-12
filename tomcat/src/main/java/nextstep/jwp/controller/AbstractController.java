package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request, HttpResponseBuilder responseBuilder) throws Exception {
        if (request.isMethod(HttpMethod.GET)) {
            return doGet(request, responseBuilder);
        }
        if (request.isMethod(HttpMethod.POST)) {
            return doPost(request, responseBuilder);
        }
        return responseBuilder.methodNotAllowed();
    }

    protected HttpResponse doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {
        return responseBuilder.methodNotAllowed();
    }

    protected HttpResponse doGet(HttpRequest request, HttpResponseBuilder responseBuilder) throws IOException {
        return responseBuilder.methodNotAllowed();
    }
}
