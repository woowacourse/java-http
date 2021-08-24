package nextstep.jwp.http.controller.custom;

import java.util.List;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.login.GetLoginController;
import nextstep.jwp.http.controller.custom.login.PostRegisterController;

public class CustomControllerFactory {
    public static List<Controller> create() {
        return List.of(
            new GetLoginController(),
            new PostRegisterController()
        );
    }
}
