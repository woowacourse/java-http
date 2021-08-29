package nextstep.jwp;

import nextstep.jwp.framework.config.FactoryConfiguration;
import nextstep.jwp.framework.webserver.WebServer;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final WebServer webServer = new WebServer(port, FactoryConfiguration.requestMapping());
        webServer.run();
    }
}
