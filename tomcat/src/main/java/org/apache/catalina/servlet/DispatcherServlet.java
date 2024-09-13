package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.net.URISyntaxException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DispatcherServlet {

    private final DefaultServlet defaultServlet;
    private final LoginController loginController;
    private final RegisterController registerController;

    public DispatcherServlet() {
        this.defaultServlet = DefaultServlet.getInstance();
        this.loginController = LoginController.getInstance();
        this.registerController = RegisterController.getInstance();
    }

    public void requestMapping(HttpRequest request, HttpResponse response) throws URISyntaxException {
        if (request.hasPath("/login")) {
            loginController.service(request, response);
        }
        if (request.hasPath("/register")) {
            registerController.service(request, response);
        }
        defaultServlet.service(request, response);
    }
}
