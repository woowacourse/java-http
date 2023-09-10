package nextstep.jwp.handler;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.controller.ViewController;
import org.apache.catalina.servlet.handler.AbstractRequestHandler;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class LoginRequestHandler extends AbstractRequestHandler {

    public LoginRequestHandler() {
        super("/login");
    }

    @Override
    protected Response doGet(final Request request) {
        return ViewController.login(request);
    }

    @Override
    protected Response doPost(final Request request) {
        return UserController.login(request);
    }

}
