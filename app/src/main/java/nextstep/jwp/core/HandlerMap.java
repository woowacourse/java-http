package nextstep.jwp.core;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.web.HttpRequest;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class HandlerMap {
    private static final Map<URI, Controller> HANDLER_MAP = new ConcurrentHashMap<>();
    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();
    private static final Controller STATIC_RESOURCE_CONTROLLER = new StaticResourceController();
    private static final Pattern STATIC_FILE_REQUEST_REGEX = Pattern.compile("(.html|.js|.css|.svg)$");

    public static Controller getController(HttpRequest request) {
        URI requestUri = request.getRequestUri();

        if (isStaticResourceRequest(requestUri)) {
            return STATIC_RESOURCE_CONTROLLER;
        }

        Controller controller = HANDLER_MAP.get(requestUri);

        if (controller != null) {
            return controller;
        }

        return NOT_FOUND_CONTROLLER;
    }

    private static boolean isStaticResourceRequest(URI requestUri) {
        return STATIC_FILE_REQUEST_REGEX.matcher(requestUri.toString()).find();
    }
}
