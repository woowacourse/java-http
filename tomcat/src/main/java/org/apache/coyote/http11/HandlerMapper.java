package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import nextstep.jwp.view.LoginHandler;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;

public class HandlerMapper {

    private static final Map<String, Function<Request, Response>> cache;

    static {
        cache = new HashMap<>();
        cache.put("GET /login ?", new LoginHandler());
    }

    public static Function<Request, Response> of(final Request request) {
        return cache.getOrDefault(request.getRequestIdentifier(), new StaticResourceHandler());
    }
}
