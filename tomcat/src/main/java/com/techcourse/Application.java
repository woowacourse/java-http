package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.startup.Controllers;
import org.apache.catalina.startup.Server;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.WAS;

public class Application {
    public static void main(String[] args) {
        Server server = new Tomcat();
        Controllers controllers = new Controllers(
                new LoginController("/login"),
                new RegisterController("/register")
        );

        new WAS(server, controllers).start();
    }
}
