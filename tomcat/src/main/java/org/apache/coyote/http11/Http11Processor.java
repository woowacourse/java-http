package org.apache.coyote.http11;

import java.net.Socket;
import java.util.Optional;
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

            Optional<String> requestedSessionId = request.findCookie("JSESSIONID");
            Optional<Session> existingSession = requestedSessionId
                    .flatMap(sessionManager::findSession);

            Session session = existingSession.orElseGet(sessionManager::createSession);
            request.attachSession(session, sessionManager);

            HttpResponse response = route(request);

            Session currentSession = request.session();
            if (requestedSessionId.filter(currentSession.getId()::equals).isEmpty()) {
                response.addHeader(
                        "Set-Cookie",
                        "JSESSIONID=" + currentSession.getId() + "; Path=/; HttpOnly"
                );
            }

            outputStream.write(response.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse route(final HttpRequest request) throws Exception {
        HttpResponse response = new HttpResponse();

        Controller controller = requestMapping.getController(request.path());
        controller.service(request, response);

        return response;
    }
}
