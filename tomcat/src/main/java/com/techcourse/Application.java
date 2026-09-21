package com.techcourse;

import com.techcourse.web.RequestMapping;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.ApplicationAdapter;
import org.apache.catalina.startup.Tomcat;
import com.techcourse.web.StaticResourceHandler;

public class Application {

    public static void main(String[] args) {
        final var applicationService = new ApplicationService();

        final var staticResourceHandler = new StaticResourceHandler();
        final var adapter = new ApplicationAdapter(new RequestMapping(applicationService, staticResourceHandler));
        final var tomcat = new Tomcat();
        tomcat.start(adapter);
    }
}
