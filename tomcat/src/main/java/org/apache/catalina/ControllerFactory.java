package org.apache.catalina;

import java.util.List;
import nextstep.jwp.controller.InternalServerExceptionController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundExceptionController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.coyote.Controller;
import org.apache.coyote.ExceptionController;

public class ControllerFactory {

    public static List<Controller> createControllers() {
        return List.of(
                new LoginController(), new StaticFileController(), new StaticFileController()
        );
    }

    public static List<ExceptionController> createExceptionControllers() {
        return List.of(
                new InternalServerExceptionController(), new NotFoundExceptionController()
        );
    }
}
