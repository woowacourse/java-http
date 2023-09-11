package nextstep.jwp.controller.page;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloWorldController extends AbstractController {

    private static final String HELLO_WORLD = "Hello world!";

    private HelloWorldController() {
    }

    public static Controller create() {
        return new HelloWorldController();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatusLine(HttpStatus.OK);
        response.setHeadersSimpleText();
        response.setResponseBody(HELLO_WORLD);
    }
}
