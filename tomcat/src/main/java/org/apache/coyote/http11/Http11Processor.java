package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestData;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (
                final var input = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream()) {

            HttpRequestData httpRequestData = HttpRequestParser.parse(input);
            HttpResponse response = new HttpResponse();

            Session session = SessionManager.getInstance().findOrCreate(httpRequestData, response);

            HttpRequest request = new HttpRequest(httpRequestData, session);

            Controller controller = ControllerResolver.resolve(request);
            controller.service(request, response);

            writeResponse(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        byte[] responseBody = response.getBody();

        StringBuilder responseHead = new StringBuilder()
                .append("HTTP/1.1")
                .append(' ')
                .append(response.getStatus().getCode())
                .append(' ')
                .append(response.getStatus().getReasonPhrase())
                .append("\r\n");

        for (Map.Entry<String, String> header
                : response.getHeaders().entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHead.append("\r\n");

        outputStream.write(
                responseHead.toString()
                        .getBytes(StandardCharsets.UTF_8)
        );
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
