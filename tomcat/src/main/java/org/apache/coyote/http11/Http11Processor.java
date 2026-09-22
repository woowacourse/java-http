package org.apache.coyote.http11;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestMapping(Map.of()));
    }

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
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

            HttpResponseWriter responseWriter = new HttpResponseWriter();
            HttpRequestReader requestReader = new HttpRequestReader();
            HttpRequest request = requestReader.read(inputStream);

            if (request == null) {
                return;
            }

            String cookieHeader = request.getHeaders().get("Cookie");

            if (cookieHeader == null) {
                cookieHeader = "";
            }

            Map<String, Cookie> cookies = parseCookies(cookieHeader);
            Cookie sessionCookie = cookies.get("JSESSIONID");
            Map<String, String> responseHeaders = new HashMap<>();

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = null;

            if (sessionCookie != null) {
                session = sessionManager.findSession(sessionCookie.getValue());
            }

            if (session == null) {
                String sessionId = UUID.randomUUID().toString();

                session = new Session(sessionId);
                sessionManager.add(session);

                sessionCookie = new Cookie("JSESSIONID", sessionId);

                responseHeaders.put(
                        "Set-Cookie",
                        sessionCookie.getName() + "=" + sessionCookie.getValue() + "; Path=/"
                );
            }

            request = request.withSession(session);

            Controller controller = requestMapping.getController(request);
            HttpResponse response = createOkResponse(CONTENT_TYPE_HTML, "Hello world!", responseHeaders);

            if (controller != null) {
                controller.service(request, response);
            }

            responseWriter.write(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createOkResponse(
            String contentType,
            String responseBody,
            Map<String, String> responseHeaders
    ) {
        Map<String, String> headers = new HashMap<>(responseHeaders);
        headers.put("Content-Type", contentType);

        return new HttpResponse(
                "HTTP/1.1",
                200,
                "OK",
                new HttpHeaders(headers),
                responseBody.getBytes(StandardCharsets.UTF_8)
        );
    }

    // 입력 예시: "yummy_cookie=choco; JSESSIONID=abc123"
    private Map<String,Cookie> parseCookies(String cookieHeader) {
        Map<String, Cookie> cookies = new HashMap<>();

        for (String part : cookieHeader.split(";")) {
            String[] nameValue = part.trim().split("=", 2);

            if (nameValue.length != 2) {
                continue;
            }

            String name = nameValue[0].trim();
            String value = nameValue[1].trim();

            cookies.put(name, new Cookie(name, value));
        }

        return cookies;
    }

}
