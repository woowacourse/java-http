package nextstep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.RequestMapping;
import nextstep.jwp.ui.Controller;
import nextstep.jwp.ui.HomeController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        registerRequestMapper();
        final var tomcat = new Tomcat();
        tomcat.start();
    }

    private static void registerRequestMapper() {
        Map<String, Controller> requestMapper = new HashMap<>();
        requestMapper.put("/", new HomeController());
        requestMapper.put("/login", new LoginController());
        requestMapper.put("/register", new RegisterController());
        RequestMapping.registerController(requestMapper);
    }
}
