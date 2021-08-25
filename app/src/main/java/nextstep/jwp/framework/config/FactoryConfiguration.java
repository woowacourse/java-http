package nextstep.jwp.framework.config;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.framework.infrastructure.mapping.HttpRequestMapping;
import nextstep.jwp.framework.infrastructure.mapping.RequestMapping;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.controller.IndexPageController;
import nextstep.jwp.framework.controller.LoginController;
import nextstep.jwp.framework.controller.RegisterController;
import nextstep.jwp.framework.controller.StaticResourceController;
import nextstep.jwp.web.application.UserService;

public class FactoryConfiguration {

    private FactoryConfiguration() {
    }

    public static RequestMapping requestMapping() {
        return new HttpRequestMapping(controllers());
    }

    private static List<Controller> controllers() {
        UserService userService = new UserService();
        return Arrays.asList(
            new LoginController(userService),
            new RegisterController(userService),
            new IndexPageController(),
            new StaticResourceController()
        );
    }
}
