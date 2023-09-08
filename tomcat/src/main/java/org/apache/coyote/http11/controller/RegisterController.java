package org.apache.coyote.http11.controller;

import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class RegisterController extends AbstractController<UserController> {

    private static final RegisterController INSTANCE = new RegisterController(UserController.INSTANCE);

    private RegisterController(UserController userController) {
        super(userController);
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected ResponseEntity doGet(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        if (requestLine.isQueryStringExisted()) {
            String account = requestLine.findQueryStringValue("account");
            String password = requestLine.findQueryStringValue("password");
            String email = requestLine.findQueryStringValue("email");

            return controller.signUp(account, password, email);
        }
        return ResponseEntity.of(HttpStatus.OK, requestLine.getPath());
    }

    @Override
    protected ResponseEntity doPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();

        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        return controller.signUp(account, password, email);
    }
}
