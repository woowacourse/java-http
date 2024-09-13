package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import org.apache.catalina.connector.CatalinaConnectionListener;
import org.apache.catalina.connector.ConnectionListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.adapter.CoyoteAdapter;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.session.SessionManager;

import java.util.Map;
import java.util.concurrent.*;

public class Application {

    public static void main(final String[] args) {

        final RequestMapper requestMapper = new RequestMapper(
                Map.of("/login", new LoginController(),
                        "/register", new RegisterController())
        );
        final ResourceController resourceController = new ResourceController();
        final SessionManager sessionManager = new SessionManager();

        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
        final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                0, 250, 0L,
                TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

        final var tomcat = createTomcat(requestMapper, resourceController, sessionManager,executorService);
        tomcat.start();
    }

    private static Tomcat createTomcat(final RequestMapper requestMapper,
                                       final ResourceController resourceController,
                                       final SessionManager sessionManager,
                                       final ExecutorService executorService) {

        final ConnectionListener connectionListener = new CatalinaConnectionListener(
                new CoyoteAdapter(requestMapper, resourceController, sessionManager),
                executorService
        );
        final Connector connector = new Connector(connectionListener);
        return new Tomcat(connector);
    }
}
