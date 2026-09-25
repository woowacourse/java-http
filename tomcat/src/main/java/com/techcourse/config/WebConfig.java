package com.techcourse.config;

import com.techcourse.controller.GetLoginController;
import com.techcourse.controller.GetRegisterController;
import com.techcourse.controller.HelloWorldController;
import com.techcourse.controller.PostLoginController;
import com.techcourse.controller.PostRegisterController;
import java.util.HashMap;
import java.util.List;
import org.apache.catalina.Manager;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.routing.Controller;
import org.apache.coyote.routing.Dispatcher;
import org.apache.coyote.routing.RequestMapping;
import org.apache.coyote.routing.RouteInfo;

public final class WebConfig {

    public Tomcat tomcat() {
        final Manager manager = new SessionManager();
        final Dispatcher dispatcher = new Dispatcher(requestMapping());
        final Connector connector = new Connector(
                connection -> new Http11Processor(connection, manager, dispatcher)
        );
        return new Tomcat(connector);
    }

    private RequestMapping requestMapping() {
        final RequestMapping requestMapping = new RequestMapping(new HashMap<>());
        configureRoutes(requestMapping);
        return requestMapping;
    }

    private void configureRoutes(final RequestMapping requestMapping) {
        List<Controller> handlers = List.of(
                new GetLoginController(),
                new PostLoginController(),
                new GetRegisterController(),
                new PostRegisterController(),
                new HelloWorldController()
        );

        List<RouteInfo> routeInfos = List.of(
                new GetLoginController(),
                new PostLoginController(),
                new GetRegisterController(),
                new PostRegisterController(),
                new HelloWorldController()
        );
        for (int i = 0; i < handlers.size(); i++) {
            final Controller handler = handlers.get(i);
            final RouteInfo routeInfo = routeInfos.get(i);

            requestMapping.add(routeInfo.getRouteKey(), handler);
        }
    }
}
