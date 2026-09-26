package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = new RequestMapping();

    private final Socket connection;
    private final SessionManager sessionManager = SessionManager.getInstance();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.from(new BufferedReader(new InputStreamReader(inputStream)));
            Session session = sessionManager.findOrCreate(request.getSessionId());
            HttpResponse response = new HttpResponse();
            response.addSessionCookie(session);

            Controller controller = requestMapping.getController(request);
            controller.service(request.withSession(session), response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
