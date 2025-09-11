package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import org.apache.catalina.Controller;
import org.apache.catalina.startup.Tomcat;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        Map<String, Class<? extends Controller>> mappingInfo = Map.of(
                "/", RootController.class,
                "/login", LoginController.class,
                "/register", RegisterController.class
        );
        final var tomcat = new Tomcat();

        tomcat.start();
    }
}
