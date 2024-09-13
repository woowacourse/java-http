package org.apache.catalina.startup;

import com.techcourse.resolver.Resolver;
import java.util.List;
import com.techcourse.resolver.Dispatcher;
import org.apache.catalina.startup.Server;

public class WAS implements Server {
    private final Server tomcat;

    public WAS(Server tomcat, List<Resolver> resolvers) {
        this.tomcat = tomcat;
        resolvers.forEach(Dispatcher::register);
    }

    @Override
    public void start() {
        tomcat.start();
    }
}
