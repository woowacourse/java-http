package com.techcourse;

import com.techcourse.controller.HttpController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.List;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.WAS;

public class Application {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        List<HttpController> resolvers = List.of(
                new LoginController("/login"),
                new RegisterController("/register")
        );
        new WAS(tomcat, resolvers).start();
    }
}
