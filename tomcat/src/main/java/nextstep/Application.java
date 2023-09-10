package nextstep;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.controller.ControllerMapper;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        ControllerMapper.register("/", new HomeController());
        ControllerMapper.register("/login", new LoginController());
        ControllerMapper.register("/register", new RegisterController());

        tomcat.start();
    }
}
