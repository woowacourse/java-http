package nextstep.jwp.handler;

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
        return generateResponse(httpRequest);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return generateResponse(httpRequest);
    }

    private HttpResponse generateResponse(final HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, HttpStatusCode.OK, "text/plain", "Hello world!");
    }
}
