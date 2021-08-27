package nextstep.jwp;

import nextstep.jwp.server.WebServer;

public class JwpApplication {

    public static void main(String[] args) {
        final int port = WebServer.defaultPortIfNull(args);
        final WebServer webServer = new WebServer(port);
        webServer.run();
    }
}
