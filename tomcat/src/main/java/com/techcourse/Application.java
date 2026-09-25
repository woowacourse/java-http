package com.techcourse;

import com.techcourse.web.Route;
import com.techcourse.web.LoginController;
import com.techcourse.web.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;

public class Application {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat(createRequestMapping());
        tomcat.start();
    }

    public static RequestMapping createRequestMapping() {
        RequestMapping mapping = new RequestMapping();
        mapping.add(Route.LOGIN.getPath(), new LoginController());
        mapping.add(Route.REGISTER.getPath(), new RegisterController());
        return mapping;
    }
}
