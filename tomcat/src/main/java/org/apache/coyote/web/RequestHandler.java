package org.apache.coyote.web;

import static org.apache.coyote.support.HttpMethod.GET;
import static org.apache.coyote.support.HttpMethod.POST;

import nextstep.jwp.controller.UserCreateController;
import nextstep.jwp.controller.UserLoginController;
import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import org.apache.coyote.support.HttpStatus;

public class RequestHandler {

    public Response handle(final Request request) {

        if (GET.isSameMethod(request.getMethod()) && request.isSameRequestUrl("/login")) {
            UserLoginRequest userLoginRequest = UserLoginRequest.from(request.getQueryParameters());
            return new UserLoginController().doGet(userLoginRequest);

        }

        if (POST.isSameMethod(request.getMethod()) && request.isSameRequestUrl("/register")) {
            UserRegisterRequest userRegisterRequest = UserRegisterRequest.from(request.parseBody());
            return new UserCreateController().doPost(userRegisterRequest);
        }

        return new BodyResponse(HttpStatus.OK, request.getHttpHeaders().toResponse(), "Hello world!");
    }
}
