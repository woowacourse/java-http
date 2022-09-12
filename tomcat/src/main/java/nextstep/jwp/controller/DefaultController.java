package nextstep.jwp.controller;

import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    private static final Controller INSTANCE = new DefaultController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private DefaultController() {
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return generateResponse();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return generateResponse();
    }

    private HttpResponse generateResponse() {
        return new HttpResponse(HttpStatusCode.OK, "text/plain", "Hello world!");
    }
}
