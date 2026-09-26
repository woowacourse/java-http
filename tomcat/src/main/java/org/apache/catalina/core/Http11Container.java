package org.apache.catalina.core;

import java.net.Socket;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;

public class Http11Container implements Container {

    private final SessionManager sessionManager;

    public Http11Container(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void execute(Socket connection) {
        new Http11Processor(connection, sessionManager).run();
    }
}
