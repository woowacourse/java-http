package nextstep;

import java.util.List;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.service.UserService;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat();
        tomcat.start(assembleControllers());
    }

    private static List<Controller> assembleControllers() {
        final var exceptionListener = new ExceptionHandler();
        final var userService = new UserService(new InMemoryUserRepository());
        return List.of(new HomeController(exceptionListener),
                new LoginController(userService, exceptionListener),
                new RegisterController(userService, exceptionListener),
                new ResourceController(exceptionListener));
    }
}
