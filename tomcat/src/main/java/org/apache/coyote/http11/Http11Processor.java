package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER =
            SessionManager.getInstance();

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestMapping(new StaticResourceController()));
    }

    public Http11Processor(
            final Socket connection,
            final RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}",
                connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final String[] requestParts = requestLine.split(" ");
            final String method = requestParts[0];
            final String path = requestParts[1].split("\\?", 2)[0];
            final Map<String, String> headers = readHeaders(reader);
            final HttpCookie cookies = new HttpCookie(headers.get("Cookie"));
            final String sessionId = cookies.get(SESSION_COOKIE_NAME);
            Session session = sessionId == null ? null : SESSION_MANAGER.findSession(sessionId);
            String setCookieHeader = null;

            if (sessionId == null || sessionId.isBlank()) {
                session = SESSION_MANAGER.createSession();
                setCookieHeader = "Set-Cookie: " + SESSION_COOKIE_NAME + "=" + session.getId();
            }

            if ("GET".equals(method) && LOGIN_PATH.equals(path) && session != null && session.getAttribute("user") != null) {
                outputStream.write(redirect("/index.html", setCookieHeader).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            if ("POST".equals(method) && (REGISTER_PATH.equals(path) || LOGIN_PATH.equals(path))) {
                final int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
                final String requestBody = readBody(reader, contentLength);
                final Map<String, String> parameters = parseForm(requestBody);

            final HttpResponse response = new HttpResponse(outputStream);
            final HttpRequest request;

            try {
                request = HttpRequest.read(inputStream);
            } catch (IllegalArgumentException e) {
                response.setStatus(400, "Bad Request");
                response.write();
                return;
            }

            if (request == null) {
                return;
            }

            attachSession(request, response);

            try {
                requestMapping.getController(request).service(request, response);
            } catch (IllegalArgumentException e) {
                response.setStatus(400, "Bad Request");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                response.setStatus(500, "Internal Server Error");
            }

            response.write();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void attachSession(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final String sessionId =
                new HttpCookie(request.getHeader("Cookie")).get("JSESSIONID");

        if (sessionId == null || sessionId.isBlank()) {
            request.getOrCreateSession(response);
            return;
        }

        final Session session = SESSION_MANAGER.findSession(sessionId);
        request.setSession(session);
    }
}
