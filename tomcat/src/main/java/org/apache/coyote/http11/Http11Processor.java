package org.apache.coyote.http11;

import java.net.Socket;
import java.util.UUID;
import java.util.function.Function;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Function<Session, RequestMapping> requestMappingFactory;

    public Http11Processor(final Socket connection,
            Function<Session, RequestMapping> requestMappingFactory) {
        this.connection = connection;
        this.requestMappingFactory = requestMappingFactory;
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
            HttpRequest request = HttpRequest.parse(inputStream);

            SessionManager sessionManager = SessionManager.getInstance();
            String sessionId = new HttpCookie(request.headers().get("cookie")).getValue("JSESSIONID");
            Session session = null;
            if (sessionId != null) {
                session = sessionManager.findSession(sessionId);
            }
            String setCookie = null;
            if (session == null) {
                session = new Session(UUID.randomUUID().toString());
                sessionManager.add(session);
                setCookie = "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly";
            }

            HttpResponse response = new HttpResponse(outputStream, setCookie);
            RequestMapping mapping = requestMappingFactory.apply(session);
            Controller controller = mapping.getController(request);
            controller.service(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
