package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.resource.StaticResourceReader;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    public static RequestMapping createRequestMapping() {
        StaticResourceReader staticResourceReader = new StaticResourceReader();
        RequestMapping requestMapping = new RequestMapping(new StaticResourceController(staticResourceReader));
        requestMapping.addController("/login", new LoginController(staticResourceReader));
        requestMapping.addController("/register", new RegisterController(staticResourceReader));
        return requestMapping;
    }
}
