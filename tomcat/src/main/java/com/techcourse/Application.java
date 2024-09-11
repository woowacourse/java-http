package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import org.apache.catalina.connector.CatalinaConnectionListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.adapter.CoyoteAdapter;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.session.SessionManager;

import java.util.Map;

public class Application {

    public static void main(final String[] args) {

        final RequestMapper requestMapper = new RequestMapper(
                Map.of("/login", new LoginController(),
                        "/register", new RegisterController())
        );
        final ResourceController resourceController = new ResourceController();
        final SessionManager sessionManager = new SessionManager();

        final CatalinaConnectionListener connectionListener = new CatalinaConnectionListener(new CoyoteAdapter(requestMapper, resourceController, sessionManager));
        final Connector connector = new Connector(connectionListener);

        final var tomcat = new Tomcat(connector);
        tomcat.start();
    }
}
