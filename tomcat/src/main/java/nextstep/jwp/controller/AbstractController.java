package nextstep.jwp.controller;

import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.requestline.Method;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.isMethod(Method.GET)) {
            return doGet(httpRequest);
        }

        if (httpRequest.isMethod(Method.POST)) {
            return doPost(httpRequest);
        }

        throw new UnsupportedMethodException();
    }

    protected abstract HttpResponse doGet(final HttpRequest httpRequest) throws Exception;

    protected abstract HttpResponse doPost(final HttpRequest httpRequest) throws Exception;
}
