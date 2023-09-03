package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.common.HandlerMapping;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class HandlerAdaptor {

    private static final Map<HandlerMapping, Function<Request, Response>> handlerMethods = new HashMap<>(
            Map.of(
                    new HandlerMapping(GET, "/login"), UserController::login,
                    new HandlerMapping(POST, "/register"), UserController::register
            )
    );

    public Response getMapping(Request request) {
        return handlerMethods.get(new HandlerMapping(GET, request.getPath()))
                .apply(request);
    }

    public Response postMapping(Request request) {
        return handlerMethods.get(new HandlerMapping(POST, request.getPath()))
                .apply(request);
    }
}
