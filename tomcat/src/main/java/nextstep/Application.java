package nextstep;

import nextstep.jwp.controller.GetController;
import nextstep.jwp.controller.PostController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.service.UserService;
import nextstep.jwp.servlet.HandlerMappings;
import nextstep.jwp.support.ResourceRegistry;
import org.apache.catalina.startup.Tomcat;
import nextstep.jwp.servlet.CustomServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        HandlerMappings.of(new GetController(new ResourceRegistry()),
                new PostController(new UserService(new InMemoryUserRepository())),
                new ResourceController(new ResourceRegistry()));
        log.info("web server start.");
        final var tomcat = new Tomcat();
        tomcat.start(new CustomServlet());
    }
}
