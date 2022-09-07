package nextstep.jwp.controller;

import org.apache.coyote.http.AbstractController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;

public class IndexController extends AbstractController {

    private static final IndexController INSTANCE = new IndexController();

    private IndexController() {
    }

    public static IndexController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath("/index.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .setBodyByPath("/500.html");
    }
}
