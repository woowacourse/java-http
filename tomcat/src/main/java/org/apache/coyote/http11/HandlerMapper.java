package org.apache.coyote.http11;

import java.util.Map;
import java.util.function.BiFunction;
import nextstep.jwp.view.LoginHandler;
import nextstep.jwp.view.RegisterHandler;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;

public class HandlerMapper {

    private static final Map<String, BiFunction<Request, Response, Response>> cache;
    private static final StaticResourceHandler staticResourceHandler;

    static {
        cache = Map.ofEntries(
                Map.entry("POST /login", new LoginHandler()),
                Map.entry("POST /register", new RegisterHandler())
        );
        staticResourceHandler = new StaticResourceHandler();
    }

    public static BiFunction<Request, Response, Response> of(final Request request) {
        return cache.getOrDefault(request.getRequestIdentifier(), staticResourceHandler);
    }
}
