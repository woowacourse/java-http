package org.apache.catalina.startup;

import com.techcourse.controller.HttpController;
import java.util.List;
import com.techcourse.controller.Dispatcher;

public class WAS implements Server {
    private final Server tomcat;

    public WAS(Server tomcat, List<HttpController> resolvers) {
        this.tomcat = tomcat;
        resolvers.forEach(Dispatcher::register);
    }

    @Override
    public void start() {
        tomcat.start();
    }
}
