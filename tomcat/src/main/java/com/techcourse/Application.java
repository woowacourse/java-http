package com.techcourse;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.mapper.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final RequestMapping requestMapping = createRequestMapping();
        final var tomcat = new Tomcat(requestMapping);
        tomcat.start();
    }

    private static RequestMapping createRequestMapping() {
        final StaticResourceController staticResourceController = new StaticResourceController();
        final RequestMapping requestMapping = new RequestMapping(staticResourceController);
        requestMapping.addMapping("/", new HomeController());
        requestMapping.addMapping("/login", new LoginController(SessionManager.getInstance(), staticResourceController));
        requestMapping.addMapping("/register", new RegisterController(staticResourceController));
        return requestMapping;
    }
}
