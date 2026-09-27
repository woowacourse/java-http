package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping = new RequestMapping();

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest request = new HttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse();

            SessionManager.getInstance().setSessionCookie(request, response);

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

}
