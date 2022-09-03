package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class MainController implements Controller {

    @Override
    public HttpResponse doService(HttpRequest request) {
        return HttpResponse.withoutLocation(
            request.getVersion(),
            "200 OK",
            request.getUri(),
            "Hello world!"
        );
    }
}
