package nextstep.jwp.presentation;

import http.HttpHeaders;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;

public class RootController extends AbstractController {

    private RootController() {
    }

    public static RootController instance() {
        return RootControllerHolder.instance;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        String responseBody = "Hello world!";

        HttpHeaders responseHeaders = setResponseHeaders(httpRequest, responseBody);

        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    private static class RootControllerHolder {

        private static final RootController instance = new RootController();
    }
}
