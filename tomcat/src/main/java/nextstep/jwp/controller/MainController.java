package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class MainController implements Controller {

    private static final Controller instance = new MainController();

    private MainController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return HttpResponse.ok("/", "Hello world!");
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        return HttpResponse.notFound();
    }
}
