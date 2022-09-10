package nextstep;

import customservlet.ChicChocServlet;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat();
        final var requestMapping = RequestMapping.getInstance();
        requestMapping.addServlet("/", new ChicChocServlet());
        tomcat.start();
    }
}
