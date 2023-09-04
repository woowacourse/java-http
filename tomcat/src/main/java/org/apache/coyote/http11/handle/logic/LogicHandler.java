package org.apache.coyote.http11.handle.logic;

import nextstep.jwp.UserFilter;
import nextstep.jwp.controller.dto.Response;
import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.handle.logic.dto.HandleResponse;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpMethod;
import org.apache.coyote.http11.request.start.HttpStartLine;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Collections;
import java.util.Map;

import static org.apache.coyote.http11.request.start.HttpMethod.GET;
import static org.apache.coyote.http11.response.HttpStatus.*;

public class LogicHandler {

    public HandleResponse handle(final HttpRequest request) {
        final HttpMethod httpMethod = request.getHttpStartLine().getHttpMethod();
        final Response loginVerification = UserFilter.verifyLogin(request.getHttpHeadersLine().getHeaders());

        if (httpMethod.equals(GET) && loginVerification.getHttpStatus().equals(NOT)) {
            return new HandleResponse(NOT, Collections.emptyMap());
        }
        if (httpMethod.equals(GET) && loginVerification.getHttpStatus().equals(FOUND)) {
            return new HandleResponse(FOUND, Collections.emptyMap());
        }
        return HandleResponse.from(executionLogic(request.getHttpStartLine(), request.getHttpBodyLine()));
    }

    private static Response executionLogic(final HttpStartLine httpStartLine, final Map<String, String> requestBody) {
        final UserController userController = new UserController();
        try {
            if (httpStartLine.getResources().contains("/login")) {
                return userController.login(requestBody);
            }
            if (httpStartLine.getResources().contains("/register")) {
                return userController.register(requestBody);
            }
            return new Response(NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.from(e.getMessage()));
        }
    }
}
