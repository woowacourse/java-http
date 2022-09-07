package nextstep.jwp.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Controller;

public class GetHomeController implements Controller {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBody(WELCOME_MESSAGE);
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        return httpRequest.getPath().equals("/");
    }
}
