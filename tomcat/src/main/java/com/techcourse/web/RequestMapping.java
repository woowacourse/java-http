package com.techcourse.web;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.ControllerResolver;
import org.apache.coyote.http11.HttpRequest;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestMapping implements ControllerResolver {

    private final Map<Route, Controller> controllers = new EnumMap<>(Route.class);

    public void add(Route route, Controller controller) {
        controllers.put(Objects.requireNonNull(route), Objects.requireNonNull(controller));
    }

    @Override
    public Optional<Controller> getController(HttpRequest request) {
        return Route.fromPath(request.getPath())
                .map(controllers::get);
    }
}
