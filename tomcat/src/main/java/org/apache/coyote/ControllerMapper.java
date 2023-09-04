package org.apache.coyote;

import org.apache.coyote.controller.LoginController;

import java.util.Arrays;

public enum ControllerMapper {

    LOGIN_CONTROLLER("/login"),
    ;

    private final String uri;

    ControllerMapper(final String uri) {
        this.uri = uri;
    }

    public static Controller getController(final String parsedUri) {
        final ControllerMapper controller = Arrays.stream(ControllerMapper.values())
                                                  .filter(controllerMapper -> controllerMapper.uri.equals(parseQueryStrings(parsedUri)))
                                                  .findFirst()
                                                  .orElseThrow(() -> new IllegalArgumentException("잘못된 API 요청입니다."));
        if (controller.uri.equals("/login")) {
            return new LoginController();
        }

        return null;
    }

    private static String parseQueryStrings(final String parsedUri) {
        if (!parsedUri.contains("?")) {
            return parsedUri;
        }
        final int index = parsedUri.indexOf("?");
        return parsedUri.substring(0, index);
    }
}
