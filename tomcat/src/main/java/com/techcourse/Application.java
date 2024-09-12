package com.techcourse;

import java.util.Map;
import com.techcourse.handler.FrontController;
import com.techcourse.handler.HandlerMapping;
import com.techcourse.handler.IndexHandler;
import com.techcourse.handler.LoginHandler;
import com.techcourse.handler.RegisterHandler;
import com.techcourse.handler.UserHandler;
import org.apache.catalina.Controller;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        HandlerMapping handlerMapping = new HandlerMapping(Map.of(
                "/", new IndexHandler(),
                "/login", new LoginHandler(),
                "/register", new RegisterHandler(),
                "/user", new UserHandler()
        ));
        Controller controller = new FrontController(handlerMapping);

        Tomcat tomcat = new Tomcat(controller);
        tomcat.start();
    }
}
