package nextstep.jwp.servlet;

import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class MainPageController extends AbstractController {

    private static final MainPageController instance = new MainPageController();

    private MainPageController() {
    }

    public static MainPageController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse doGet(HttpRequest request, HttpResponse response) {
        String body = "Hello world!";
        return response.contentType(request.getAccept())
                       .body(body);
    }
}
