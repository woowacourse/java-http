package nextstep;

import static nextstep.ApplicationConfiguration.frontController;
import static nextstep.ApplicationConfiguration.resourceHandler;
import static nextstep.ApplicationConfiguration.welcomeHandler;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Context;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        final Context context = tomcat.addContainer("/", frontController());

        context.addHandler(welcomeHandler());
        context.addHandler(resourceHandler());

        tomcat.start();
    }
}
