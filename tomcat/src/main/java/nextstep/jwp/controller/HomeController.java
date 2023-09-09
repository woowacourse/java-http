package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.line.ResponseStatus;

public class HomeController extends AbstractController {

    private static final String DEFAULT_BODY = "Hello world!";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(GET, "/")) {
            doGet(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseMessage(ResponseStatus.OK, DEFAULT_BODY);
    }
}
