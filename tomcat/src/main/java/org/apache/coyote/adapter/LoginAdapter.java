package org.apache.coyote.adapter;

import org.apache.coyote.handler.LoginController;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public class LoginAdapter implements Adapter {

    @Override
    public Response doHandle(Request request) {
        LoginController loginController = new LoginController();
        loginController.login(request);

        return new ResourceAdapter().doHandle(request);
    }
}
