package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.handler.LoginHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController{

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.ok("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        if (LoginHandler.login(request.getParams())) {
            response.redirect("/index.html");
            return;
        }
        response.unauthorized("/401.html");
    }
}
