package nextstep.jwp;

import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwpApplication {
    public static void main(String[] args) {
        try {
            int port = WebServer.defaultPortIfNull(args);
            final WebServer webServer = new WebServer(port);
            webServer.run();
        } catch (Exception e) {

        }
    }
}
