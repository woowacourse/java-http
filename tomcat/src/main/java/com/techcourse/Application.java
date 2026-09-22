package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = new RequestMapping(
                Map.of(
                        "/register", new RegisterController(),
                        "/login", new LoginController(),
                        "/index.html", new StaticResourceController("index.html", "text/html;charset=utf-8"),
                        "/401.html", new StaticResourceController("401.html", "text/html;charset=utf-8"),
                        "/css/styles.css", new StaticResourceController("css/styles.css", "text/css;charset=utf-8"),
                        "/js/scripts.js", new StaticResourceController("js/scripts.js", "text/javascript;charset=utf-8"),
                        "/assets/chart-area.js", new StaticResourceController("assets/chart-area.js", "text/javascript;charset=utf-8"),
                        "/assets/chart-bar.js", new StaticResourceController("assets/chart-bar.js", "text/javascript;charset=utf-8"),
                        "/assets/chart-pie.js", new StaticResourceController("assets/chart-pie.js", "text/javascript;charset=utf-8")
                )
        );

        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }
}
