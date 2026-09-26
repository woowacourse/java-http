package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        String requestPath = "unknown";
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            RequestMapping requestMapping = new RequestMapping();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            HttpRequest httpRequest = HttpRequest.from(reader);
            requestPath = httpRequest.getRequestPath();
            Http11Response httpResponse = Http11Response.empty();
            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            httpResponse.writeTo(outputStream);

        } catch (Exception e) {
            log.error("HTTP 요청 처리 실패. path={}", requestPath, e);
        }
    }
}
