package nextstep;

import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RegisterHandler;
import nextstep.jwp.handler.RootHandler;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.addHandler("/", new RootHandler());
        tomcat.addHandler("/login", new LoginHandler());
        tomcat.addHandler("/register", new RegisterHandler());
        tomcat.start();
    }
}
