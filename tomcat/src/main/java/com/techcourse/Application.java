package com.techcourse;

import com.techcourse.controller.ErrorController;
import com.techcourse.controller.GreetingController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import java.util.List;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Dispatcher;

public class Application {

    private static final List<Class<? extends AbstractController>> CONTROLLER_CLASSES = List.of(
            GreetingController.class, LoginController.class, RegisterController.class
    );

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat(setupDispatcher());
        tomcat.start();
    }

    private static Dispatcher setupDispatcher() {
        RequestMapper requestMapper = new RequestMapper();
        requestMapper.registerControllers(CONTROLLER_CLASSES);
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapper);
        dispatcher.setNotFoundHandler(NotFoundController.class);
        dispatcher.setUnresolvedErrorHandler(ErrorController.class);
        return dispatcher;
    }
}
