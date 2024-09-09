package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.controller.HandlerContainer;

public class Application {

    public static void main(String[] args) {
        HandlerContainer.add(new LoginController());
        HandlerContainer.add(new RegisterController());

        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
