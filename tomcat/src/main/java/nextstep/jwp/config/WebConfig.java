package nextstep.jwp.config;

import nextstep.jwp.controller.ErrorHandler;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.Config;

public class WebConfig extends Config {

    @Override
    protected void addControllers() {
        addController(HelloController.getINSTANCE());
        addController(LoginController.getINSTANCE());
        addController(ResourceController.getINSTANCE());
        addController(RegisterController.getINSTANCE());
    }

    @Override
    protected void setExceptionHandler() {
        setExceptionHandler(ErrorHandler.getINSTANCE());
    }
}
