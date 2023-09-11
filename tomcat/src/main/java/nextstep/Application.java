package nextstep;

import org.apache.catalina.startup.Tomcat;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.catalina.controller.ControllerStatus;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.addController(new ControllerStatus("/"), new RootController());
        tomcat.addController(new ControllerStatus("/login"), new LoginController());
        tomcat.addController(new ControllerStatus("/register"), new RegisterController());
        tomcat.start();
    }
}
