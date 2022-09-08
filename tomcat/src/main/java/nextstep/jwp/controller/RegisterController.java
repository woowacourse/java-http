package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.ok("/register.html");
    }
}
