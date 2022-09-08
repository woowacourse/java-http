package org.apache.coyote.http11.util;

import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.StaticResourceController;
import nextstep.jwp.presentation.WelcomeController;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.common.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> REQUEST_MAPPING;

    static {
        REQUEST_MAPPING = Map.of(
                "/", WelcomeController.getInstance(),
                "/login", LoginController.getInstance(),
                "/register", RegisterController.getInstance()
        );
    }

    private RequestMapping() {
    }

    public static Controller findController(final HttpRequest request) {
        return REQUEST_MAPPING.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(request.getPath()))
                .map(Entry::getValue)
                .findFirst()
                .orElse(StaticResourceController.getInstance());
    }
}
