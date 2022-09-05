package org.apache.coyote.web;

import static org.apache.coyote.support.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.support.HttpMethod.GET;
import static org.apache.coyote.support.HttpMethod.POST;

import nextstep.jwp.controller.UserCreateController;
import nextstep.jwp.controller.UserLoginController;
import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.BodyResponse;
import org.apache.coyote.web.response.HttpResponse;

public class RequestHandler {

    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(GET) && httpRequest.isSameRequestUrl("/login")) {
            UserLoginRequest userLoginRequest = UserLoginRequest.from(httpRequest.getQueryParameters());
            return new UserLoginController().doGet(userLoginRequest);

        }

        if (httpRequest.isSameHttpMethod(POST) && httpRequest.isSameRequestUrl("/register")) {
            UserRegisterRequest userRegisterRequest = UserRegisterRequest.from(httpRequest.parseBody());
            return new UserCreateController().doPost(userRegisterRequest);
        }

        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE.getValue(), ContentType.STRINGS.getValue()));
        return new BodyResponse(HttpStatus.OK, httpHeaders, "Hello world!");
    }
}
