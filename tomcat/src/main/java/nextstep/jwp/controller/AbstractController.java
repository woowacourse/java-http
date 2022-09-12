package nextstep.jwp.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.support.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse getResponse(HttpRequest httpRequest) throws Exception {
        if (HttpMethod.GET == httpRequest.getHttpMethod()) {
            return doGet(httpRequest);
        }

        if (HttpMethod.POST == httpRequest.getHttpMethod()) {
            return doPost(httpRequest);
        }
        return ControllerAdvice.handleNotFound();
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return ControllerAdvice.handleNotFound();
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return ControllerAdvice.handleNotFound();
    }
}
