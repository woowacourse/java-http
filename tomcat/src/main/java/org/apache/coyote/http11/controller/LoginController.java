package org.apache.coyote.http11.controller;

import nextstep.jwp.model.Request;
import org.apache.coyote.http11.service.LoginService;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.FormData;
import nextstep.jwp.vo.Response;

import java.io.IOException;

public class LoginController implements Controller {

    private static final LoginController INSTANCE = new LoginController();

    @Override
    public Response respond(Request request) throws IOException {
        FileName fileName = request.getFileName();
        FormData requestBody = request.getBody();

        return LoginService.signIn(requestBody.get("account"), requestBody.get("password"));
    }

    private LoginController() {
    }

    public static Controller getInstance() {
        return INSTANCE;
    }
}
