package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestMapping());
    }

    Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
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
            final HttpRequest request = new HttpRequest(inputStream);
            initializeSession(request);

            final HttpResponse response = new HttpResponse();
            requestMapping.getController(request).service(request, response);
            if (request.isNewSession()) {
                response.addCookie("JSESSIONID", request.getSession().getId());
            }
            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("HTTP 요청 처리 중 오류가 발생했습니다.", e);
        }
    }

    private void initializeSession(final HttpRequest request) {
        final String requestedSessionId = request.getCookie("JSESSIONID");
        final boolean isNewSession = requestedSessionId == null;
        final String sessionId = isNewSession ? UUID.randomUUID().toString() : requestedSessionId;
        request.setSession(SessionManager.getOrCreate(sessionId), isNewSession);
    }

    public static final class SessionManager {

        private static final Map<String, Session> sessions = new HashMap<>();

        private SessionManager() {
        }

        public static void save(final Session session) {
            sessions.put(session.getId(), session);
        }

        public static Optional<Session> findById(final String id) {
            return Optional.ofNullable(sessions.get(id));
        }

        public static Session getOrCreate(final String id) {
            return sessions.computeIfAbsent(id, Session::new);
        }

        public static void remove(final String id) {
            sessions.remove(id);
        }
    }

    public static final class Session {

        private final String id;
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        private Session(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public Object getAttribute(final String name) {
            return attributes.get(name);
        }

        public void setAttribute(final String name, final Object value) {
            attributes.put(name, value);
        }

        public void removeAttribute(final String name) {
            attributes.remove(name);
        }

        public void invalidate() {
            attributes.clear();
            SessionManager.remove(id);
        }
    }
}
