package nextstep.jwp.controller;

import common.ResponseStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String URL = "/";

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.consistsOf(URL);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseMessage(ResponseStatus.OK, DEFAULT_BODY);
    }
}
