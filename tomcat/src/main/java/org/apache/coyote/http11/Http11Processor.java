package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpResponse response = new HttpResponse();

            try {
                final HttpRequest request = HttpRequest.from(inputStream);
                final Session existingSession = request.getSession(false);
                handleRequest(request, response);
                setSessionCookie(request, response, existingSession);

            } catch (IllegalArgumentException e) {
                response = new HttpResponse();
                response.setStatus(400, "Bad Request");
                response.setHeader("Content-Type", "text/plain;charset=utf-8");
                response.setBody("Bad Request".getBytes(StandardCharsets.UTF_8));
            }catch(Exception e){
                log.error("요청 처리 중 오류가 발생했습니다.", e);
                response = new HttpResponse();
                response.setStatus(500, "Internal Server Error");
                response.setHeader("Content-Type", "text/plain;charset=utf-8");
                response.setBody("Internal Server Error".getBytes(StandardCharsets.UTF_8));
            }
            response.writeTo(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setSessionCookie(final HttpRequest request, final HttpResponse response,
                                  final Session existingSession) {
        final Session currentSession = request.getSession(false);
        if (currentSession != null && currentSession != existingSession) {
            response.addCookie("JSESSIONID", currentSession.getId());
            return;
        }
        if (request.getCookie("JSESSIONID") == null) {
            response.addCookie("JSESSIONID", UUID.randomUUID().toString());
        }
    }

    private void handleRequest(final HttpRequest request,
                               final HttpResponse response) throws Exception {
        final Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }

}
