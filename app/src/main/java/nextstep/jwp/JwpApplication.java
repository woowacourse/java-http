package nextstep.jwp;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.mvc.DispatcherServlet;

public class JwpApplication {

    public static void main(String[] args) {
        int port = WebServer.defaultPortIfNull(args);
        final ApplicationContext applicationContext = new DefaultApplicationContext("nextstep");
        final DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        final WebServer webServer = new WebServer(port, dispatcherServlet);
        webServer.run();
    }
}
