package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;
    private final RequestMapping requestMapping;

    public Http11Processor(
            final Socket connection,
            final Manager sessionManager,
            final RequestMapping requestMapping
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
            outputStream.write(createResponse(inputStream).serialize());
            outputStream.flush();
        } catch (IOException e) {
            log.error("HTTP 응답을 전송하지 못했습니다.", e);
        }
    }

    private HttpResponse createResponse(InputStream inputStream) {
        HttpRequest request;
        try {
            request = HttpRequestParser.parse(inputStream);
        } catch (IllegalArgumentException | IOException e) {
            log.warn("잘못된 HTTP 요청입니다.", e);
            return HttpResponse.badRequest(HttpVersion.HTTP_1_1);
        }

        try {
            attachExistingSession(request);
            HttpResponse response = requestMapping.getController(request).service(request);
            applySessionCookie(request, response);
            return response;
        } catch (Exception e) {
            log.error("HTTP 요청 처리 중 오류가 발생했습니다.", e);
            return HttpResponse.internalServerError(
                    request.getVersion(),
                    "<h1>500 Internal Server Error</h1>".getBytes(StandardCharsets.UTF_8)
            );
        }
    }

    private void attachExistingSession(HttpRequest request) throws IOException {
        String sessionId = request.getSessionId().orElse(null);
        if (sessionId == null) {
            return;
        }

        HttpSession session = sessionManager.findSession(sessionId);
        if (session != null) {
            request.setSession(session);
        }
    }

    private void applySessionCookie(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        if (session == null || !session.isNew()) {
            return;
        }

        response.addCookie("JSESSIONID=" + session.getId() + "; Path=/");
        if (session instanceof SimpleSession simpleSession) {
            simpleSession.markEstablished();
        }
    }
}
