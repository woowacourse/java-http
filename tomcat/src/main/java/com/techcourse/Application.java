package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.dispatcher.Dispatcher;
import org.apache.catalina.dispatcher.ViewResolver;
import org.apache.catalina.dispatcher.handler.ControllerHandler;
import org.apache.catalina.dispatcher.handler.HandlerMapping;
import org.apache.catalina.dispatcher.handler.StaticHandler;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.session.SessionManager;

import java.util.List;
import java.util.Map;

public class Application {

    public static void main(String[] args) {

        SessionManager sessionManager = new SessionManager();
        LoginController loginController = new LoginController(sessionManager);
        RegisterController registerController = new RegisterController();
        RequestMapping requestMapping = new RequestMapping(Map.of(
                "/login", loginController,
                "/login.html", loginController,
                "/register", registerController
        ));

        StaticHandler staticHandler = new StaticHandler();
        ControllerHandler controllerHandler = new ControllerHandler(requestMapping);
        HandlerMapping handlerMapping = new HandlerMapping(List.of(controllerHandler, staticHandler));
        ViewResolver viewResolver = new ViewResolver();
        Dispatcher dispatcher = new Dispatcher(handlerMapping, viewResolver);
        final var tomcat = new Tomcat(dispatcher);
        tomcat.start();
    }
}
