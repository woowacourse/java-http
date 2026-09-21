package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.dispatcher.Dispatcher;
import org.apache.catalina.dispatcher.ViewResolver;
import org.apache.catalina.dispatcher.handler.ControllerHandler;
import org.apache.catalina.dispatcher.handler.HandlerMapping;
import org.apache.catalina.dispatcher.handler.StaticHandler;
import org.apache.coyote.http11.session.SessionManager;

import java.util.List;
import java.util.Map;

public class AppConfig {

    private final SessionManager sessionManager;

    public AppConfig() {
        this(new SessionManager());
    }

    public AppConfig(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Dispatcher dispatcher() {
        return new Dispatcher(handlerMapping(), viewResolver());
    }

    private HandlerMapping handlerMapping() {
        return new HandlerMapping(List.of(new ControllerHandler(requestMapping()), new StaticHandler()));
    }

    private RequestMapping requestMapping() {
        LoginController loginController = new LoginController(sessionManager);
        return new RequestMapping(Map.of(
                "/login", loginController,
                "/login.html", loginController,
                "/register", new RegisterController()
        ));
    }

    private ViewResolver viewResolver() {
        return new ViewResolver();
    }

}
