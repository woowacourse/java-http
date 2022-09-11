package nextstep.jwp.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.model.FileName;
import nextstep.jwp.model.FormData;
import nextstep.jwp.model.Response;

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
