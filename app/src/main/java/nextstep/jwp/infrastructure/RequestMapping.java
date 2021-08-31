package nextstep.jwp.infrastructure;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.exception.NoMatchingControllerException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class RequestMapping {
    private static final Controller staticResourceController = new StaticResourceController();
    private static final List<Controller> controllers = new ArrayList<>();

    static {
        final UserService userService = new UserService();
        controllers.add(new LoginController(userService));
        controllers.add(new RegisterController(userService));
        controllers.add(staticResourceController);
    }

    public Controller findController(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(it -> it.canHandle(httpRequest))
                .findAny()
                .orElseThrow(NoMatchingControllerException::new);
    }

    public Controller getStaticResourceController() {
        return staticResourceController;
    }
}
