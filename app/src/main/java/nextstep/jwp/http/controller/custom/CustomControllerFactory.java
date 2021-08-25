package nextstep.jwp.http.controller.custom;

import java.util.List;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.login.GetLoginController;
import nextstep.jwp.http.controller.custom.login.GetRegisterController;
import nextstep.jwp.http.controller.custom.login.PostLoginController;
import nextstep.jwp.http.controller.custom.login.PostRegisterController;

public class CustomControllerFactory {
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
