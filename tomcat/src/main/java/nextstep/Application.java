package nextstep;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        new Tomcat()
                .addController("/", new HomeController())
                .addController("/login", new LoginController())
                .addController("/register", new RegisterController())
                .start();
    }
}
