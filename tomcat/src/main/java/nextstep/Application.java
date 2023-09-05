package nextstep;

import nextstep.jwp.servlet.DispatcherServlet;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        var servlet = new DispatcherServlet();
        final var tomcat = new Tomcat(servlet);
        tomcat.start();
    }
}
