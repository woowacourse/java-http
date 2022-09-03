package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class MainController implements Controller {

    @Override
    public HttpResponse doService(HttpRequest request) {
        return HttpResponse.ok("/", "Hello world!");
    }
}
