package nextstep.jwp.presentation;

import http.HttpHeaders;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import java.io.IOException;

public class DefaultController extends AbstractController {

    private DefaultController() {
    }

    public static DefaultController instance() {
        return DefaultControllerHolder.instance;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws IOException {
        String responseBody = readResource(httpRequest);
        HttpHeaders responseHeaders = setResponseHeaders(httpRequest, responseBody);
        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    private static class DefaultControllerHolder {

        private static final DefaultController instance = new DefaultController();
    }
}
