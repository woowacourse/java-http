package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController implements Controller {

    @Override
    public HttpResponse process(final HttpRequest httpRequest) {
        return HttpResponse.ok(httpRequest.getRequestURL().getPath(), "Hello world!");
    }
}
