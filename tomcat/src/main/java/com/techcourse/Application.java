package com.techcourse;

import com.techcourse.web.HomeController;
import com.techcourse.web.LoginController;
import com.techcourse.web.RegisterController;
import com.techcourse.web.RequestMapping;
import com.techcourse.web.Route;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    public static RequestMapping createRequestMapping() {
        RequestMapping mapping = new RequestMapping();
        mapping.add(Route.HOME, new HomeController());
        mapping.add(Route.LOGIN, new LoginController());
        mapping.add(Route.REGISTER, new RegisterController());
        return mapping;
    }
}
