package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;
    private final RequestMapping requestMapping;

    public Http11Processor(
            Socket connection,
            Manager sessionManager,
            RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.requestMapping = requestMapping;
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
            HttpRequest request = HttpRequest.readFrom(inputStream);
            if (request == null) {
                return;
            }

            Optional<Session> existingSession = request.findCookie("JSESSIONID")
                    .flatMap(sessionManager::findSession);

            Session session = existingSession.orElseGet(sessionManager::createSession);
            request.attachSession(session);

            HttpResponse response = route(request);

            if (existingSession.isEmpty() && !response.hasHeader("Set-Cookie")) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            }

            outputStream.write(response.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse route(final HttpRequest request) throws Exception {
        HttpResponse response = new HttpResponse();

        Controller controller = requestMapping.getController(request);
        controller.service(request, response);

        return response;
    }
}
