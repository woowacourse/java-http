package nextstep;

import nextstep.jwp.servlet.ControllerAdvice;
import nextstep.jwp.servlet.DispatcherServlet;
import nextstep.jwp.servlet.RequestMapper;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        var servlet = new DispatcherServlet(new ControllerAdvice(), new RequestMapper());
        final var tomcat = new Tomcat(servlet);
        tomcat.start();
    }
}
