package nextstep;

import java.util.List;
import mvc.controller.AbstractPathController;
import mvc.controller.FrontController;
import mvc.controller.LoginController;
import mvc.controller.RegisterController;
import mvc.controller.mapping.RequestMapping;
import nextstep.jwp.application.UserService;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Context;
import org.apache.coyote.handler.WelcomeHandler;
import servlet.Controller;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        final Context context = tomcat.addContainer("/", frontController());

        context.addHandler(new WelcomeHandler());

        tomcat.start();
    }

    private static UserService userService() {
        return new UserService();
    }

    private static Controller frontController() {
        return new FrontController(requestMapping());
    }

    private static RequestMapping requestMapping() {
        final List<AbstractPathController> controllers = List.of(
                new LoginController("/login", userService()),
                new RegisterController("/register", userService()));

        return new RequestMapping(controllers);
    }
}
