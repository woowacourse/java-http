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
                .filter(mappedRequestUri -> canMatch(mappedRequestUri, requestUri))
                .findAny()
                .map(mappedRequestUri -> controller);
    }

    private boolean canMatch(String mappedRequestUri, String requestUri) {
        final String allWildCard = "*";

        if (!mappedRequestUri.contains(allWildCard)) {
            return mappedRequestUri.equals(requestUri);
        }

        int indexOfWildCard = mappedRequestUri.indexOf(allWildCard);
        String beforeWildCard = mappedRequestUri.substring(0, indexOfWildCard);
        String afterWildCard = mappedRequestUri.substring(indexOfWildCard + 1);

        if (afterWildCard.contains(allWildCard)) {
            throw new IllegalStateException("잘못된 mappedRequestUri 입니다.");
        }

        return requestUri.startsWith(beforeWildCard) && requestUri.endsWith(afterWildCard);
    }
}
