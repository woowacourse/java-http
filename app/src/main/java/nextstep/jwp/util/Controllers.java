package nextstep.jwp.util;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ExtraController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;

public enum Controllers {
    LOGIN("/login", new LoginController((LoginService) Services.LOGIN.service())),
    REGISTER("/register", new RegisterController((RegisterService) Services.REGISTER.service())),
    STATIC("/static", new StaticController());

    private static final Controller EXTRA_CONTROLLER = new ExtraController();

    private final String uri;
    private final Controller controller;

    Controllers(String uri, Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static Controller matchController(HttpRequest httpRequest) {
        final String urlWithoutQuery = httpRequest.getRequestURLWithoutQuery();
        if (StaticResources.matchFromHeader(httpRequest)) {
            return STATIC.controller;
        }
        return Arrays.stream(Controllers.values())
            .filter(element -> element.isSameURL(urlWithoutQuery))
            .findAny()
            .map(element -> element.controller)
            .orElse(EXTRA_CONTROLLER);
    }

    private boolean isSameURL(String targetURL) {
        return this.uri.equals(targetURL);
    }

}
