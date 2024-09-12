package org.apache.coyote.http11;

import java.util.List;
import java.util.Optional;

public class RequestMapping {

    private final List<String> mappedRequestUris;

    private final Controller controller;

    public RequestMapping(Controller controller, String... requestUris) {
        this.mappedRequestUris = List.of(requestUris);
        this.controller = controller;
    }

    public Optional<Controller> getControllerIfMapped(String requestUri) {
        return mappedRequestUris.stream()
                .filter(requestUri::contains)
                .findAny()
                .map(mappedRequestUri -> controller);
    }
}
