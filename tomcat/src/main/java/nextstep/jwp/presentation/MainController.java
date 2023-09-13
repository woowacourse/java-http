package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class MainController extends AbstractController{

    private static final MainController INSTANCE = new MainController();

    private MainController() {
    }

    public static MainController getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        return httpResponse
                .addBaseHeader(httpRequest.getContentType())
                .addBody("Hello world!");
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        return null;
    }
}
