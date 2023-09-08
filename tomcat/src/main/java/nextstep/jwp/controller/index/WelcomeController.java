package nextstep.jwp.controller.index;

import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.handler.mapper.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class WelcomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ok("Hello world!");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UnsupportedMethodException();
    }
}
