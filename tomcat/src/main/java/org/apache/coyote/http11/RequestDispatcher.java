package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;

public class RequestDispatcher {

    private final LoginController loginController;

    public RequestDispatcher() {
        this.loginController = new LoginController();
    }

    public void requestMapping(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        if (path.equals("/login")) {
            loginController.service(request, response);
        }
    }
}
