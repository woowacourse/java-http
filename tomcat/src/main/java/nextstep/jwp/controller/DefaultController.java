package nextstep.jwp.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String body = "Hello world!";
        response.updateBody(body);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    private DefaultController() {
    }

    public static DefaultController getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final DefaultController instance = new DefaultController();
    }
}
