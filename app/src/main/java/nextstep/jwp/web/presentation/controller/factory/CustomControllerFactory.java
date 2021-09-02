package nextstep.jwp.web.presentation.controller.factory;

import nextstep.jwp.server.handler.controller.Controller;
import nextstep.jwp.web.presentation.controller.login.GetLoginController;
import nextstep.jwp.web.presentation.controller.login.GetRegisterController;
import nextstep.jwp.web.presentation.controller.login.PostLoginController;
import nextstep.jwp.web.presentation.controller.login.PostRegisterController;
import nextstep.jwp.web.presentation.controller.welcome.WelcomePageController;

import java.util.List;

public class CustomControllerFactory {

    private CustomControllerFactory() {
    }

    public static List<Controller> create() {
        return List.of(
            new PostLoginController(),
            new PostRegisterController(),
            new GetLoginController(),
            new GetRegisterController(),
            new WelcomePageController()
        );
    }
}
