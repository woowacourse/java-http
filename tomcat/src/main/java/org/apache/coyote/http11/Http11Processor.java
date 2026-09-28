package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.routing.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
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

            final RequestLine requestLine = RequestLine.parse(readLine(inputStream));
            if (requestLine == null) return;
            final HttpHeaders requestHeaders = HttpHeaders.from(readHeaders(inputStream));
            final byte[] messageBody = inputStream.readNBytes(requestHeaders.getContentLength());

            final HttpHeaders responseHeaders  = new HttpHeaders(new HashMap<>());
            Session session;
            final HttpCookie httpCookie = HttpCookie.from(requestHeaders.get("Cookie"));
            if (httpCookie.contains("JSESSIONID")) {
                final String sessionId = httpCookie.get("JSESSIONID");
                session = SessionManager.findSession(sessionId);
                if (session == null) {
                    session = new Session(sessionId);
                    SessionManager.add(session);
                }
            } else {
                final String sessionId = String.valueOf(UUID.randomUUID());
                session = new Session(sessionId);
                SessionManager.add(session);
                responseHeaders.add("Set-Cookie", "JSESSIONID=" + sessionId);
            }

            final HttpRequest request = new HttpRequest(requestLine, requestHeaders, messageBody, session);
            final HttpResponse response = HttpResponse.createDefaultResponse(requestLine.protocolVersion(), responseHeaders);

            final Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readLine(final InputStream inputStream) throws IOException {
        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int current;
            while ((current = inputStream.read()) != -1) {
                if (current == '\r') {
                    final int next = inputStream.read();

                    if (next == '\n') {
                        break;
                    }

                    buffer.write(current);

                    if (next != -1) {
                        buffer.write(next);
                    }

                    continue;
                }

                buffer.write(current);
            }

            return buffer.toString();
        }
    }

    private List<String> readHeaders(final InputStream reader) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line;
        while  (!(line = readLine(reader)).isBlank()) {
            headers.add(line);
        }
        return headers;
    }
}
