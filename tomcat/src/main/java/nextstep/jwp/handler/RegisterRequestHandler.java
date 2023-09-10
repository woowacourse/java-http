package nextstep.jwp.handler;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.controller.ViewController;
import org.apache.catalina.servlet.handler.Servlet;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class RegisterRequestHandler extends Servlet {

    public RegisterRequestHandler() {
        super("/register");
    }

    @Override
    protected Response doGet(final Request request) {
        return ViewController.register(request);
    }

    @Override
    protected Response doPost(final Request request) {
        return UserController.register(request);
    }

}
