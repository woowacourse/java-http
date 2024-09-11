package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

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
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(input);
            HttpResponse response = new HttpResponse();

            processRequest(request, response);
            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processRequest(HttpRequest request, HttpResponse response) {
        setSession(request, response);

        FrontController frontController = FrontController.getInstance();
        Controller controller = frontController.mapController(request.getPath());
        controller.service(request, response);
    }

    private void setSession(HttpRequest request, HttpResponse response) {
        if(!request.hasCookie("JSESSIONID")) {
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = new Session();
            sessionManager.add(session);
            response.setCookie(new Cookie("JSESSIONID", session.getId()));
        }
    }
}
