package org.apache.coyote.http11;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class HandlerMapping {

    public ResponseEntity extractResponseEntity(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        HttpMethod httpMethod = requestLine.getHttpMethod();
        String path = requestLine.getPath();

        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            UserController userController = new UserController(new UserService());
            RequestBody requestBody = request.getRequestBody();
            if ("/login".equals(path)) {
                return userController.login(requestBody);
            }
            if ("/register".equals(path)) {
                return userController.signUp(requestBody);
            }
        }

        return new ResponseEntity(HttpMethod.GET, HttpStatus.OK, path);
    }

}
