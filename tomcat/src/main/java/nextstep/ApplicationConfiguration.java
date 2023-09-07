package nextstep;

import java.util.List;
import mvc.controller.AbstractPathController;
import mvc.controller.FrontController;
import mvc.controller.LoginController;
import mvc.controller.RegisterController;
import mvc.controller.mapping.RequestMapping;
import nextstep.jwp.application.UserService;
import org.apache.coyote.Handler;
import org.apache.coyote.handler.ResourceHandler;
import org.apache.coyote.handler.WelcomeHandler;
import servlet.Controller;

public final class ApplicationConfiguration {

    public static UserService userService() {
        return new UserService();
    }

    public static Controller frontController() {
        return new FrontController(requestMapping());
    }

    public static RequestMapping requestMapping() {
        final List<AbstractPathController> controllers = List.of(
                new LoginController("/login", userService()),
                new RegisterController("/register", userService()));

        return new RequestMapping(controllers);
    }

    public static Handler welcomeHandler() {
        return new WelcomeHandler();
    }

    public static Handler resourceHandler() {
        return new ResourceHandler();
    }

    private ApplicationConfiguration() {
    }
}
