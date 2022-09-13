package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    private DefaultController() {
    }

    public static Controller getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.ok(request.getPathUri());
    }

    private static class LazyHolder {
        private static final DefaultController INSTANCE = new DefaultController();
    }
}
