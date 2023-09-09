package nextstep;

import nextstep.jwp.servlet.LoginController;
import nextstep.jwp.servlet.MainPageController;
import nextstep.jwp.servlet.RegisterController;
import nextstep.jwp.servlet.ResourceController;
import org.apache.catalina.FrontController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        FrontController adaptor = FrontController.getInstance();
        adaptor.addController("/", MainPageController.getInstance());
        adaptor.addController("/login", LoginController.getInstance());
        adaptor.addController("/register", RegisterController.getInstance());
        adaptor.setResourceHandler(ResourceController.getInstance());

        final var tomcat = new Tomcat(adaptor);
        tomcat.start();
    }
}
