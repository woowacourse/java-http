package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private static final Map<String, Controller> REQUEST_MAPPER = new HashMap<>();

    static {
        REQUEST_MAPPER.put(ROOT_PATH, new RootController());
        REQUEST_MAPPER.put(LOGIN_PATH, new LoginController());
        REQUEST_MAPPER.put(REGISTER_PATH, new RegisterController());
    }

    public Controller getController(final HttpRequest request) {
        return REQUEST_MAPPER.getOrDefault(request.getUri().getPath(), new PageController());
    }
}
