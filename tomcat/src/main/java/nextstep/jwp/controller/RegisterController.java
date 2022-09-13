package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.handler.RegisterHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.ok("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        if (RegisterHandler.register(request.getParams())) {
            response.redirect("/index.html");
            return;
        }
        response.redirect("/register.html");
    }
}
