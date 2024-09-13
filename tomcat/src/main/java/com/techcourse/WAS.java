package com.techcourse;

import com.techcourse.resolver.Resolver;
import java.util.List;
import com.techcourse.resolver.Dispatcher;

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
