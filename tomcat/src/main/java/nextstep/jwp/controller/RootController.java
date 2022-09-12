package nextstep.jwp.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController implements Handler {

    private static final RootController INSTANCE = new RootController();

    private RootController() {
    }

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setResponseBodyContent("Hello world!");
        httpResponse.setOkHttpStatusLine();
        httpResponse.setOKHeader(HTML);
    }

    public static RootController getINSTANCE() {
        return INSTANCE;
    }
}
