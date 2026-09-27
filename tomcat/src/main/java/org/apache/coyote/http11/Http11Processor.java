package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;

    public Http11Processor(final Socket connection, final Manager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse(outputStream);
            String path = request.getPath();

            Controller controller = ControllerMapper.getController(path);
            if (controller == null) {
                response.forward(getDefaultPath(path));
                return;
            }

            String sessionId = request.getCookies().getCookie("JSESSIONID");
            Session session = sessionManager.findSession(sessionId);
            if (session == null) {
                session = sessionManager.createSession();
                response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            }

            request.setSession(session);
            controller.service(request, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
