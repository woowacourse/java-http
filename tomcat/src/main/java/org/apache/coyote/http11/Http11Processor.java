package org.apache.coyote.http11;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping mapping;
    private final SessionResolver sessionResolver;

    public Http11Processor(final Socket connection, RequestMapping mapping, SessionResolver sessionResolver) {
        this.connection = connection;
        this.mapping = mapping;
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream input = connection.getInputStream();
             OutputStream output = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(input);
            HttpResponse response = new HttpResponse();

            Session session = sessionResolver.resolveOrCreate(request, response);
            request.setSession(session);

            Controller controller = mapping.getController(request);
            controller.service(request, response);

            response.writeTo(output);
        } catch (Exception e) {
            log.error("요청 처리 실패", e);
        }
    }

}
