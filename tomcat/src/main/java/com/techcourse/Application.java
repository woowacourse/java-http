package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.SignupController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        
        tomcat.addController("/login", new LoginController());
        tomcat.addController("/register", new SignupController());
        
        tomcat.start();
    }
}
