package org.apache.coyote.http11;

import java.util.List;
import java.util.Optional;

public class RequestMapping {

    private final List<String> mappedRequestUris;

    private final Controller controller;

    public static RequestMapping from(Controller controller, String... requestUris) {
        return new RequestMapping(List.of(requestUris), controller);
    }

    public RequestMapping(List<String> mappedRequestUris, Controller controller) {
        this.mappedRequestUris = mappedRequestUris;
        this.controller = controller;
    }

    public Optional<Controller> getControllerIfMapped(String requestUri) {
        return mappedRequestUris.stream()
                .filter(requestUri::contains)
                .findAny()
                .map(s -> controller);
    }
}
