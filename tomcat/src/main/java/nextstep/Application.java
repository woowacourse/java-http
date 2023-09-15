package nextstep;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.catalina.DispatcherServlet;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        RequestMapping requestMapping = new RequestMapping().addHandler("/", new RootController())
                .addHandler("/login", new LoginController())
                .addHandler("/register", new RegisterController());
        tomcat.start(new DispatcherServlet(requestMapping));
    }
}
