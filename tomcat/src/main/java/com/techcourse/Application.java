package com.techcourse;

import com.techcourse.controller.ApplicationController;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.ApplicationDispatcher;
import org.apache.catalina.startup.Tomcat;
import com.techcourse.web.StaticResourceHandler;

public class Application {

    public static void main(String[] args) {
        final var applicationService = new ApplicationService();
        final var applicationController = new ApplicationController(applicationService);
        final var staticResourceHandler = new StaticResourceHandler();
        final var dispatcher = new ApplicationDispatcher(applicationController, staticResourceHandler);
        final var tomcat = new Tomcat();
        tomcat.start(dispatcher);
    }
}
