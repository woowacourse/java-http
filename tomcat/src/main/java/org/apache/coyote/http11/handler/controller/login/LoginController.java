package org.apache.coyote.http11.handler.controller.login;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.handler.controller.base.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        System.out.println("login");
        super.service(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doPost(request, response);
    }
}
