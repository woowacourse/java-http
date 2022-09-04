package nextstep;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.ui.DashboardController;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat(List.of(new DashboardController(new InMemoryUserRepository())));
        tomcat.start();
    }
}
