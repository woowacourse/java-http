package nextstep.jwp.controller;

import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ErrorController extends AbstractController {

    private static final ErrorController INSTANCE = new ErrorController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private ErrorController() {
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
        return HttpResponse.of(HttpStatusCode.NOT_FOUND, "/404.html");
    }
}
