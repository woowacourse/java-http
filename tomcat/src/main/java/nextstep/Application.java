package nextstep;

import nextstep.jwp.HomeController;
import nextstep.jwp.LoginController;
import nextstep.jwp.RegisterController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.addController("/", new HomeController());
        tomcat.addController("/login", new LoginController());
        tomcat.addController("register", new RegisterController());
        tomcat.start();
    }
}
