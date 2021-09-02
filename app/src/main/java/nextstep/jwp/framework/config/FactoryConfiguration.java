package nextstep.jwp.framework.config;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.framework.infrastructure.mapping.HttpRequestMapping;
import nextstep.jwp.framework.infrastructure.mapping.RequestMapping;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.controller.custom.IndexPageController;
import nextstep.jwp.framework.controller.custom.LoginController;
import nextstep.jwp.framework.controller.custom.RegisterController;
import nextstep.jwp.framework.controller.standard.StaticResourceController;
import nextstep.jwp.framework.infrastructure.random.UUIdGenerator;
import nextstep.jwp.web.application.UserService;

public class FactoryConfiguration {

    private FactoryConfiguration() {
    }

    public static RequestMapping requestMapping() {
        return new HttpRequestMapping(customControllers(), standardControllers());
    }

    private static List<Controller> customControllers() {
        UserService userService = new UserService();
        return Arrays.asList(
            new LoginController(userService, new UUIdGenerator()),
            new RegisterController(userService),
            new IndexPageController()
        );
    }

    private static List<Controller> standardControllers() {
        return Arrays.asList(new StaticResourceController());
    }
}
