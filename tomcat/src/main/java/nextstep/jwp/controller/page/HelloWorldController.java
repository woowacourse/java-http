package nextstep.jwp.controller.page;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatusLine;

public class HelloWorldController extends AbstractController {

    private static final String HELLO_WORLD = "Hello world!";

    private HelloWorldController() {
    }

    public static Controller create() {
        return new HelloWorldController();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return new HttpResponse(ResponseStatusLine.create(HttpStatus.OK),
                HttpHeaders.createSimpleText(),
                HELLO_WORLD);
    }
}
