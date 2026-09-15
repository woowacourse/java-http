package com.techcourse;

import com.techcourse.controller.ApplicationController;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.ApplicationDispatcher;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var applicationService = new ApplicationService();
        final var applicationController = new ApplicationController(applicationService);
        final var dispatcher = new ApplicationDispatcher(applicationController);
        final var tomcat = new Tomcat();
        tomcat.start(dispatcher);
    }
}
