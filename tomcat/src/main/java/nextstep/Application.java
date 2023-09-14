package nextstep;

import org.apache.coyote.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
