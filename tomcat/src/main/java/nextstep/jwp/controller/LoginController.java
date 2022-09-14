package nextstep.jwp.controller;

import nextstep.jwp.model.FormData;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.service.LoginService;

public class LoginController implements Controller {

    private static final LoginController INSTANCE = new LoginController();

    @Override
    public Response respond(Request request) {
        FormData requestBody = request.getBody();

        return LoginService.signIn(requestBody.get("account"), requestBody.get("password"));
    }

    private LoginController() {
    }

    public static Controller getInstance() {
        return INSTANCE;
    }
}
