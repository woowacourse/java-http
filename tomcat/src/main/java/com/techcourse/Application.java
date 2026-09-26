package com.techcourse;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat(requestMapping());
        tomcat.start();
    }

    public static RequestMapping requestMapping() {
        return new RequestMapping(Map.of(
                "/login", new LoginController(),
                "/register", new RegisterController(),
                "/index.html", new StaticResourceController("static/index.html", "text/html;charset=utf-8"),
                "/401.html", new StaticResourceController("static/401.html", "text/html;charset=utf-8"),
                "/css/styles.css", new StaticResourceController("static/css/styles.css", "text/css;charset=utf-8")
        ), new DefaultController());
    }
}
