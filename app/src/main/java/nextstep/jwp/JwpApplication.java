package nextstep.jwp;

import nextstep.jwp.framework.domain.NetworkHandler;
import nextstep.jwp.framework.infrastructure.HttpHandler;
import nextstep.jwp.framework.infrastructure.mapping.HttpRequestMapping;
import nextstep.jwp.framework.webserver.WebServer;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final NetworkHandler networkHandler = new HttpHandler(new HttpRequestMapping());
        final WebServer webServer = new WebServer(port, networkHandler);
        webServer.run();
    }
}
