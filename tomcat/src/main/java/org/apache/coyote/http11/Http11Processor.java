package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

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

            HttpRequest request = HttpRequest.from(inputStream);
            HttpResponse response = new HttpResponse(outputStream);

            final Optional<String> requestedSessionId = request.getCookies().getValue("JSESSIONID");
            final Session session = request.getSession(true);
            if (requestedSessionId.filter(session.getId()::equals).isEmpty()) {
                response.addCookie("JSESSIONID", session.getId());
            }

            final Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
