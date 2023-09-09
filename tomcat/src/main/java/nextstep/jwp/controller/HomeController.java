package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.line.ResponseStatus;

public class HomeController extends AbstractController {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String URL = "/";

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.consistsOf(URL);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseMessage(ResponseStatus.OK, DEFAULT_BODY);
    }
}
