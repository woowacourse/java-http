package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomePageController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public enum ControllerMapping {
    LOGIN(url -> url.contains("/login"), controller -> new LoginController()),
    REGISTER(url -> url.contains("/register"), controller -> new RegisterController()),
    HOME(url -> url.contains("/"), controller -> new HomePageController());

    private final Predicate<String> findUrl;
    private final Function<String, AbstractController> findController;

    ControllerMapping(Predicate<String> findUrl, Function<String, AbstractController> findController) {
        this.findUrl = findUrl;
        this.findController = findController;
    }

    public static Controller find(String requestUri) {
        return findUrl(requestUri)
                .findController.apply(requestUri);
    }

    private static ControllerMapping findUrl(String requestUri) {
        return Arrays.stream(values())
                .filter(controllerMapper -> controllerMapper.findUrl.test(requestUri))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
