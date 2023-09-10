package nextstep;

import java.util.Map;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.RequestMapper;
import org.apache.catalina.startup.Tomcat;

public class Application {
    private static final Map<String, AbstractController> controllers = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private static final AbstractController defaultController = new ResourceController();

    public static void main(String[] args) {
        RequestMapper requestMapping = new RequestMapper(
                controllers,
                defaultController
        );
        final var tomcat = new Tomcat();
        tomcat.start(requestMapping);
    }
}
