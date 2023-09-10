package nextstep.jwp.handler;

import java.io.IOException;
import javassist.NotFoundException;
import nextstep.jwp.controller.UserController;
import nextstep.jwp.controller.ViewController;
import org.apache.catalina.core.servlet.Servlet;
import org.apache.catalina.core.servlet.ServletResponse;
import org.apache.coyote.http11.request.Request;

public class RegisterRequestHandler extends Servlet {

    public RegisterRequestHandler() {
        super("/register");
    }

    @Override
    protected void doGet(final Request request, final ServletResponse response) throws NotFoundException, IOException {
        response.set(ViewController.register(request));
    }

    @Override
    protected void doPost(final Request request, final ServletResponse response) {
        response.set(UserController.register(request));
    }

}
