package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String HTTP_VERSION = "1.1";

    private final Socket connection;
    private final Map<String, Controller> controllers = new HashMap<>();

    public Http11Processor(final Socket connection) {
        controllers.put("login", new LoginController());
        controllers.put("register", new RegisterController());
        controllers.put("default", new DefaultController());
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest request = new HttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse(outputStream);

            log.info("connect host: {}, port: {}, path:{}", connection.getInetAddress(), connection.getPort(), request.getUri());
            Session session = getSession(request);
            request.setSession(session);
            response.setVersion(HTTP_VERSION);

            handle(request, response);

            response.response();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(HttpRequest request, HttpResponse response) throws Exception {
        if (request.equalsUri("/login") || request.equalsUri("/login.html")) {
            controllers.get("login").service(request, response);
            return;
        }

        if (request.equalsUri("/register") || request.equalsUri("/register.html")) {
            controllers.get("register").service(request, response);
            return;
        }

        controllers.get("default").service(request, response);
    }

    private Session getSession(HttpRequest request) {
        if(!request.containsSessionId()){
            return null;
        }

        String sessionId = request.getSessionId();
        return SESSION_MANAGER.findSession(sessionId);
    }
}
