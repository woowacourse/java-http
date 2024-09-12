package com.techcourse.app;

import com.techcourse.framework.FrameworkDispatcher;
import com.techcourse.framework.RequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Dispatcher;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = new RequestMapping();
        Dispatcher dispatcher = new FrameworkDispatcher(requestMapping);
        final var tomcat = new Tomcat(dispatcher);
        tomcat.start();
    }
}
