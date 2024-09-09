package com.techcourse;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.executor.RequestExecutors;
import org.apache.coyote.http11.session.SessionManager;

public class Application {

    public static void main(final String[] args) {
        final RequestExecutors requestExecutors = new RequestExecutors();
        final SessionManager sessionManager = new SessionManager();
        final Connector connector = new Connector(requestExecutors, sessionManager);

        final var tomcat = new Tomcat(connector);
        tomcat.start();
    }
}
