package com.techcourse;

import com.techcourse.resolver.HomeResolver;
import com.techcourse.resolver.LoginResolver;
import com.techcourse.resolver.RegisterResolver;
import com.techcourse.resolver.Resolver;
import java.util.List;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        List<Resolver> resolvers = List.of(
                new LoginResolver(),
                new RegisterResolver()
        );
        new WAS(tomcat, resolvers).start();
    }
}
