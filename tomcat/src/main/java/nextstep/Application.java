package nextstep;

import nextstep.jwp.controller.FrontHandler;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http.session.SessionManager;

public class Application {

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(new FrontHandler(new SessionManager()));
        tomcat.start();
    }
}
