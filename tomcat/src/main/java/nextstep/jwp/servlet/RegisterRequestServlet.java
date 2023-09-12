package nextstep.jwp.servlet;

import java.io.IOException;
import javassist.NotFoundException;
import nextstep.jwp.controller.UserController;
import nextstep.jwp.controller.ViewController;
import org.apache.catalina.core.servlet.HttpServlet;
import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;

public class RegisterRequestServlet extends HttpServlet {

    public RegisterRequestServlet() {
        super("/register");
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws NotFoundException, IOException {
        response.set(ViewController.register(request));
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        response.set(UserController.register(request));
    }

}
