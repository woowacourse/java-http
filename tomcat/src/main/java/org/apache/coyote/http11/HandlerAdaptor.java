package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.common.HandlerMapping;
import org.apache.coyote.http11.common.HandlerMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class HandlerAdaptor {

    private static final Map<HandlerMapping, HandlerMethod> handlerMethods = new HashMap<>(
            Map.of(
                    new HandlerMapping(POST, "/login"), UserController::login,
                    new HandlerMapping(POST, "/register"), UserController::register
            )
    );

    public Response getMapping(Request request) {
        return handlerMethods.get(new HandlerMapping(GET, request.getPath()))
                .handle(request);
    }

    public Response postMapping(Request request) {
        return handlerMethods.get(new HandlerMapping(POST, request.getPath()))
                .handle(request);
    }
}
