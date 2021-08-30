package nextstep.jwp;

import nextstep.jwp.framework.manager.DynamicWebManager;
import nextstep.jwp.framework.manager.RequestManager;
import nextstep.jwp.framework.manager.StaticResourceManager;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final RequestManager requestManager = new RequestManager(new StaticResourceManager(), new DynamicWebManager());
        final WebServer webServer = new WebServer(port, requestManager);
        webServer.run();
    }
}
