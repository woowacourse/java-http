package nextstep.jwp.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    private static final RootController INSTANCE = new RootController();

    private RootController() {
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setResponseBodyContent("Hello world!");
        response.setOkHttpStatusLine();
        response.setOKHeader(HTML);
    }

    public static RootController getINSTANCE() {
        return INSTANCE;
    }
}
