package nextstep.jwp.http.controller;

import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;

import java.util.Arrays;
import java.util.function.Predicate;

public enum RequestMapping {
    RESOURCE(uri -> uri.contains("."), new ResourceController()),
    LOGIN(uri -> uri.equals("/login"), new LoginController()),
    REGISTER(uri -> uri.equals("/register"), new RegisterController())
    ;

    private final Predicate<String> predicate;
    private final Controller controller;

    RequestMapping(Predicate<String> predicate, Controller controller) {
        this.predicate = predicate;
        this.controller = controller;
    }

    public static Controller getController(String uri) {
        return Arrays.stream(values())
                .filter(handlerMapper -> handlerMapper.isSatisfied(uri))
                .findAny()
                .map(mapper -> mapper.controller)
                .orElseGet(NotFoundController::new)
                ;
    }

    private boolean isSatisfied(String uri) {
        return this.predicate.test(uri);
    }
}
