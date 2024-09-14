package com.techcourse;

import com.techcourse.controller.LoginRequestController;
import com.techcourse.controller.RootRequestController;
import com.techcourse.controller.SignupRequestController;
import java.util.Map;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        Map<String, Object> mapping = Map.ofEntries(
                Map.entry("/login", new LoginRequestController()),
                Map.entry("/register", new SignupRequestController()),
                Map.entry("/", new RootRequestController())
        );
        tomcat.start(mapping);
    }
}
