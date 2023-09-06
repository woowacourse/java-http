package org.apache.coyote.http11.handler;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public abstract class UserHandler implements HttpHandler {

    protected final UserController userController;

    protected UserHandler() {
        this.userController = new UserController(new UserService());
    }

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return doGet(httpRequest.getRequestLine());
        }
        return doPost(httpRequest.getRequestBody());
    }

    protected abstract ResponseEntity doGet(RequestLine requestLine);

    protected abstract ResponseEntity doPost(RequestBody requestBody);

}
