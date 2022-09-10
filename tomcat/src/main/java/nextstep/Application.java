package nextstep;

import nextstep.jwp.servlet.DispatcherServlet;
import org.apache.catalina.core.ServletContainer;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final ServletContainer servletContainer = new ServletContainer();
        servletContainer.registerServlet("/", new DispatcherServlet());
        final var tomcat = new Tomcat(servletContainer);
        tomcat.start();
    }
}
