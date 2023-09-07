package nextstep.jwp.controller.page;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseLine;

public class HelloWorldPageController implements Controller {

    private static final String HELLO_WORLD = "Hello world!";

    private HelloWorldPageController() {
    }

    public static Controller create() {
        return new HelloWorldPageController();
    }

    @Override
    public HttpResponse process(final HttpRequest request) {
        return new HttpResponse(ResponseLine.create(HttpStatus.OK),
                HttpHeaders.createSimpleText(),
                HELLO_WORLD);
    }
}
