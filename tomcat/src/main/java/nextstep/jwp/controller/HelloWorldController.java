package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HelloWorldController extends AbstractController {

    private static final String BODY = "Hello world!";
    private static final HelloWorldController helloWorldController = new HelloWorldController();

    private HelloWorldController() {
    }

    public static HelloWorldController getInstance() {
        return helloWorldController;
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.TEXT_HTML.getType());
        response.setContent(BODY);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}
