package com.techcourse;

import com.techcourse.controller.HelloController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import jakarta.controller.Controller;
import org.apache.catalina.container.RequestMapping;
import org.apache.catalina.startup.Tomcat;

import java.util.HashMap;
import java.util.Map;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.start(requestMapping());
    }

    public static RequestMapping requestMapping() {
        Map<String, Controller> mapping = new HashMap<>();
        mapping.put("/", new HelloController());
        mapping.put("/login", new LoginController());
        mapping.put("/register", new RegisterController());

        return new RequestMapping(mapping, new NotFoundController());
    }
}
