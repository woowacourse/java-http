package org.apache.coyote.http11;

import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * 소켓의 InputStream으로 클라이언트가 보낸 HTTP 요청을 읽고,
 * 요청을 처리할 Controller를 찾아 실행한 뒤 OutputStream으로 HTTP 응답을 전송한다.
 *
 * 요청을 해석하는 책임은 HttpRequest가, 응답을 HTTP 형식으로 만드는 책임은 HttpResponse가,
 * URI와 HTTP 메서드에 따른 실제 처리 책임은 각 Controller가 담당한다.
 */
public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final RequestMapping requestMapping;

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

            HttpRequest request = HttpRequest.from(inputStream);
            HttpResponse response = new HttpResponse();

            addSessionCookie(request, response);

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            sendResponse(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookie(HttpRequest request, HttpResponse response) {
        HttpCookie httpCookie = request.getHttpCookie();
        if (!httpCookie.hasJsessionId()) {
            String sessionId = UUID.randomUUID().toString();
            Session session = new Session(sessionId);
            SessionManager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
        }
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
