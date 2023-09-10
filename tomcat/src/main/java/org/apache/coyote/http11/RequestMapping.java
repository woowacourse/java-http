package org.apache.coyote.http11;

import nextstep.jwp.controller.*;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private static final Map<String, Controller> mappers = new HashMap<>();

    static {
        mappers.put("/", new HomeController());
        mappers.put("/login", new LoginController());
        mappers.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(final HttpRequest request) {
        final String uri = request.getRequestLine().getPath();
        final String path = uri.split("\\?")[0];
        return mappers.getOrDefault(path, new ResourceController());
    }
}
