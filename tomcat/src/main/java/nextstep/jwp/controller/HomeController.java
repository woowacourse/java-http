package nextstep.jwp.controller;

import org.apache.coyote.http.AbstractController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;

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
        return HttpResponse.init(HttpStatusCode.OK)
                .setBody(WELCOME_MESSAGE);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .setBodyByPath("/500.html");
    }
}
