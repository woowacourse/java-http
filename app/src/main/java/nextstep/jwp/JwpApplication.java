package nextstep.jwp;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.WebServer;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final WebServer webServer = new WebServer(port);
        BeanFactory.init();
        webServer.run();
    }
}
