package com.techcourse;

import com.techcourse.executor.LoginGetExecutor;
import com.techcourse.executor.LoginPostExecutor;
import com.techcourse.executor.RegisterGetExecutor;
import com.techcourse.executor.RegisterPostExecutor;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.ServerSocketBuilder;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.executor.RequestExecutors;
import org.apache.coyote.http11.session.SessionManager;

import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    public static void main(final String[] args) {

        final ServerSocket serverSocket = ServerSocketBuilder.createServerSocket();
        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final RequestExecutors requestExecutors = new RequestExecutors(
                List.of(new LoginGetExecutor(), new RegisterGetExecutor(), new RegisterPostExecutor(), new LoginPostExecutor()
                ));
        final SessionManager sessionManager = new SessionManager();
        final Connector connector = new Connector(serverSocket, executorService, requestExecutors, sessionManager);

        final var tomcat = new Tomcat(connector);
        tomcat.start();
    }
}
