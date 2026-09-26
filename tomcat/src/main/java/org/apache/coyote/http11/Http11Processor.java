package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpCookie;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpResponse response = new HttpResponse();
            HttpRequest parsedRequest = HttpRequest.from(reader);
            HttpRequest request = parsedRequest.withSession(resolveSession(parsedRequest, response));

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Session resolveSession(HttpRequest request, HttpResponse response) {
        HttpCookie cookie = request.getCookie();
        if (cookie.hasJSessionId()) {
            Session session = sessionManager.findSession(cookie.getJSessionId());
            if (session != null) {
                return session;
            }
        }

        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        response.addCookie(HttpCookie.JSESSIONID, session.getId());
        return session;
    }
}
