package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwpApplication {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public static void main(String[] args) {
        try {
            int port = WebServer.defaultPortIfNull(args);
            final WebServer webServer = new WebServer(port);
            webServer.run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
