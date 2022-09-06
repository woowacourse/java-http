package nextstep;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import nextstep.jwp.controller.UserController;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var dispatcherServlet = DispatcherServlet.getInstance();
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
