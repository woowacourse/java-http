package nextstep.jwp.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

import static org.apache.coyote.common.ContentType.HTML;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(HTML);
        response.setBody("Hello world!");
    }
}
