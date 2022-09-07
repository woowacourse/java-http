package nextstep.jwp.controller;

import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ofOk("Hello world!");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UnsupportedMethodException();
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isPath("/");
    }
}
