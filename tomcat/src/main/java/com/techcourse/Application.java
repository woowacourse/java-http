package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = RequestMapping.of(
                new LoginController(),
                new RegisterController()
        );
        Tomcat tomcat = new Tomcat();
        tomcat.start(requestMapping);
    }
}
