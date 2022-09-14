package nextstep.jwp.presentation;

import http.header.HttpHeaders;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.response.HttpStatus;

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
