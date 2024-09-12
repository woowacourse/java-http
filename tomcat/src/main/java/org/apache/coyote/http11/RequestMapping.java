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
        if (mappedRequestUri.contains("*")) {
            int indexOfWildCard = mappedRequestUri.indexOf("*");
            String beforeWildCard = mappedRequestUri.substring(0, indexOfWildCard);
            if (beforeWildCard.isEmpty()) { // *ㅁㄴㅇㅁㄴ 형태
                String afterWildCard = mappedRequestUri.replaceFirst("\\*", "");
                if (afterWildCard.contains("*")) {
                    throw new IllegalStateException("잘못된 mappedRequestUri 입니다.");
                }
                return requestUri.endsWith(afterWildCard);
            }
            // sda*asda 형태
            String afterWildCard = mappedRequestUri.replace(beforeWildCard + "*", "");
            return requestUri.startsWith(beforeWildCard) && requestUri.endsWith(afterWildCard);
        }
        return mappedRequestUri.equals(requestUri);
    }
}
