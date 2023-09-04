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
                    new HandlerMapping(POST, "/login"), UserController::login,
                    new HandlerMapping(POST, "/register"), UserController::register
            )
    );

    public Response getMapping(Request request) {
        /// TODO: 2023/09/04 매핑되는 메서드가 없을 때 예외처리
        return handlerMethods.get(new HandlerMapping(GET, request.getPath()))
                .apply(request);
    }

    public Response postMapping(Request request) {
        /// TODO: 2023/09/04 매핑되는 메서드가 없을 때 예외처리
        return handlerMethods.get(new HandlerMapping(POST, request.getPath()))
                .apply(request);
    }
}
