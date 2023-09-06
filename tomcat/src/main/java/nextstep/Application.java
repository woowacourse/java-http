package nextstep;

import nextstep.jwp.application.UserService;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Container;
import nextstep.handler.LoginHandler;
import nextstep.handler.LoginPageHandler;
import nextstep.handler.RegisterHandler;
import nextstep.handler.RegisterPageHandler;
import nextstep.handler.WelcomeHandler;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        final Container container = tomcat.addContainer("/");

        container.addHandler(new WelcomeHandler());
        container.addHandler(new LoginHandler("/login", userService()));
        container.addHandler(new LoginPageHandler("/login", "login.html"));
        container.addHandler(new RegisterPageHandler("/register", "register.html"));
        container.addHandler(new RegisterHandler("/register", userService()));

        tomcat.start();
    }

    private static UserService userService() {
        return new UserService();
    }
}
