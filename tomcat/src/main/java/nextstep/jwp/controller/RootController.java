package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import static org.apache.coyote.common.ContentType.HTML;

public class RootController extends AbstractController {

    private static final Controller INSTANCE = new RootController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(HTML);
        response.setBody("Hello world!");
    }
}
