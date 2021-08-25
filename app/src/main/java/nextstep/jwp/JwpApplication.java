package nextstep.jwp;

import nextstep.jwp.framework.domain.NetworkHandler;
import nextstep.jwp.framework.infrastructure.config.FactoryConfiguration;
import nextstep.jwp.framework.webserver.WebServer;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final NetworkHandler networkHandler = FactoryConfiguration.networkHandler();
        final WebServer webServer = new WebServer(port, networkHandler);
        webServer.run();
    }
}
