package nextstep.jwp.controller.index;

import nextstep.jwp.controller.base.AbstractController;
import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class WelcomeController extends AbstractController {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws Exception {
        return super.handle(httpRequest);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ofOk("Hello world!");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UnsupportedMethodException();
    }
}
