package nextstep;

import java.util.Map;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final Tomcat tomcat = new Tomcat(Map.of(
                "/", new HomeController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        ));
        tomcat.start();
    }
}
