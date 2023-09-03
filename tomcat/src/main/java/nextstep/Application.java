package nextstep;

import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final Tomcat tomcat = new Tomcat();
        tomcat.start();
    }

}
