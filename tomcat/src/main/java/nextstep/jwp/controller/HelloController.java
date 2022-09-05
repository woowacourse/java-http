package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HelloController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return HttpResponse.ofOk("Hello world!");
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isPath("/");
    }
}
