package nextstep.jwp.controller;

import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HelloController extends AbstractController {

    private static final HelloController INSTANCE = new HelloController();

    public static HelloController getINSTANCE() {
        return INSTANCE;
    }

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

    private HelloController() {
    }
}
