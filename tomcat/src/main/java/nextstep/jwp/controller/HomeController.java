package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String WELCOME_MESSAGE = "Hello world!";
    private static final HomeController INSTANCE = new HomeController();

    private HomeController() {
    }

    public static HomeController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.OK)
                .setBody(WELCOME_MESSAGE);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.METHOD_NOT_ALLOWED)
                .setBody(HttpStatusCode.METHOD_NOT_ALLOWED.getMessage());
    }
}
