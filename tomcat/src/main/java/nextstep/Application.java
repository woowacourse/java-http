package nextstep;

import nextstep.jwp.servlet.CustomServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.servlet.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat(new SessionRepository());
        tomcat.start(new CustomServlet());
    }
}
