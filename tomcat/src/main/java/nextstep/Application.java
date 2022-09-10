package nextstep;

import nextstep.jwp.ChicChocServlet;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat();
        final RequestMapping requestMapping = RequestMapping.getRequestMapping();
        requestMapping.addServlet("/", new ChicChocServlet());
        tomcat.start();
    }
}
