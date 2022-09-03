package nextstep;

import org.apache.catalina.startup.Tomcat;
import nextstep.jwp.servlet.CustomServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat();
        tomcat.start(new CustomServlet());
    }
}
