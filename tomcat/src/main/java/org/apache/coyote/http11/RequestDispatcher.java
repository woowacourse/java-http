package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;

public class RequestDispatcher {

    private final LoginController loginController;
    private final RegisterController registerController;

    public RequestDispatcher() {
        this.loginController = new LoginController();
        this.registerController = new RegisterController();
    }

    public void requestMapping(HttpRequest request, HttpResponse response) {
        if (request.hasPath("/login")) {
            loginController.service(request, response);
        }
        if (request.hasPath("/register")) {
            registerController.service(request, response);
        }
    }
}
