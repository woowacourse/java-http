package nextstep;

import nextstep.servlet.DispatcherServletManager;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(new DispatcherServletManager());
        tomcat.start();
    }
}
