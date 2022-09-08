package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class CommonController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getPath().equals("/")) {
            response.ok("/index.html");
            return;
        }
        response.ok(request.getPath());
    }
}
