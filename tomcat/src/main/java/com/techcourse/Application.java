package com.techcourse;

import com.techcourse.executor.LoginController;
import com.techcourse.executor.RegisterController;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.ServerSocketBuilder;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.executor.RequestExecutors;
import org.apache.coyote.http11.session.SessionManager;

import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(final String[] args) {

        final ServerSocket serverSocket = ServerSocketBuilder.createServerSocket();
        final ExecutorService executorService = new ThreadPoolExecutor(
                0, 250, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

        final RequestExecutors requestExecutors = new RequestExecutors(
                Map.of("/login", new LoginController(),
                        "/register", new RegisterController())
                );
        final SessionManager sessionManager = new SessionManager();
        final Connector connector = new Connector(serverSocket, executorService, requestExecutors, sessionManager);

        final var tomcat = new Tomcat(connector);
        tomcat.start();
    }
}
