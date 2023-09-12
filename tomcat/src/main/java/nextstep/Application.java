package nextstep;

import nextstep.jwp.JwpRequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.handler.RequestMapping;

public class Application {

    private static final RequestMapping requestMapping = new JwpRequestMapping();

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
