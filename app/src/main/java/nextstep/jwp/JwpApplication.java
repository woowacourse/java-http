package nextstep.jwp;

import nextstep.jwp.infrastructure.RequestMapping;
import nextstep.jwp.infrastructure.WebServer;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final WebServer webServer = new WebServer(port, new RequestMapping());
        webServer.run();
    }
}
