package org.apache.coyote.http11;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
import org.apache.catalina.Controller;
import org.apache.catalina.Manager;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int READ_TIMEOUT_MILLISECONDS = 5000;
    private final Socket connection;
    private final RequestMapping requestMapping;
    private final Manager manager;

    public Http11Processor(final Socket connection, RequestMapping requestMapping, Manager manager) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.manager = manager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
        ) {
            connection.setSoTimeout(READ_TIMEOUT_MILLISECONDS);

            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse();
            String sessionId = request.getCookie("JSESSIONID");

            Session session = manager.findSession(sessionId);
            if (session == null) {
                sessionId = UUID.randomUUID().toString();
                session = new Session(sessionId);

                manager.add(session);
                response.setCookie("JSESSIONID", sessionId);
            }

            request.setSession(session);

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            response.writeTo(outputStream);
        } catch (SocketTimeoutException exception) {
            log.warn("소켓에서 데이터 읽기를 기다리는 시간이 초과되었습니다.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
