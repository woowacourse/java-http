package nextstep.jwp.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.model.FileName;
import nextstep.jwp.model.FormData;
import nextstep.jwp.model.Response;

import java.io.IOException;

public class RegisterController implements Controller {

    private static final RegisterController INSTANCE = new RegisterController();

    @Override
    public Response respond(Request request) throws IOException {
        FileName fileName = request.getFileName();
        FormData requestBody = request.getBody();

        return RegisterService.signUp(
                requestBody.get("account"),
                requestBody.get("password"),
                requestBody.get("email")
        );
    }

    private RegisterController() {
    }

    public static Controller getInstance() {
        return INSTANCE;
    }
}
