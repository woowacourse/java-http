package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.RequestMappings;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.start(makeRequestMapping());
    }

    private static RequestMappings makeRequestMapping() {
        return new RequestMappings(
                new RequestMapping(new LoginController(), "/login", "/login.html"),
                new RequestMapping(new RegisterController(), "/register", "/register.html"),
                new RequestMapping(new StaticResourceController(), "*.js", "*.css", "*.html", "/", "/index",
                        "/index.html", "", "*")
        );
    }
}
