package nextstep.jwp.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.updatePage(request.getParsedRequestURI());
        response.addHeader("Set-Cookie", "JSESSIONID=" + request.getSessionId());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {

    }

    private NotFoundController() {
    }

    public static NotFoundController getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final NotFoundController instance = new NotFoundController();
    }
}
