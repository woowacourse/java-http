package org.apache.coyote.http11.controller;

import nextstep.jwp.model.Request;
import org.apache.coyote.http11.service.RegisterService;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.FormData;
import nextstep.jwp.vo.Response;

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
