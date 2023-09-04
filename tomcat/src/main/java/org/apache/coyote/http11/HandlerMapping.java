package org.apache.coyote.http11;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.service.UserService;

public class HandlerMapping {

    public ResponseEntity extractResponseEntity(RequestLine requestLine) {
        String path = requestLine.getPath();

        if (path.equals("/login")) {
            UserController userController = new UserController(new UserService());
            if (requestLine.isQueryStringExisted()) {
                String account = requestLine.findQueryStringValue("account");
                String password = requestLine.findQueryStringValue("password");

                return userController.login(account, password);
            }
        }

        return ResponseEntity.of(HttpStatus.OK, path);
    }

}
