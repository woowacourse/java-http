package nextstep.jwp.http.controller.custom;

import java.util.List;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.login.LoginController;

public class CustomControllerFactory {
    public static List<Controller> create() {
        return List.of(
            new LoginController()
        );
    }
}
