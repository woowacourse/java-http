package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

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
