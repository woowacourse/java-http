package com.techcourse;

import java.util.Map;
import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.UserController;
import com.techcourse.servlet.DispatcherServlet;
import com.techcourse.servlet.RequestMapping;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = new RequestMapping(Map.of(
                "/", new IndexController(),
                "/login", new LoginController(),
                "/register", new RegisterController(),
                "/user", new UserController()
        ));
        Servlet servlet = new DispatcherServlet(requestMapping);

        Tomcat tomcat = new Tomcat(servlet);
        tomcat.start();
    }
}
