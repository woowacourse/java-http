package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import kokodak.RequestMapper;
import kokodak.StringReader;
import kokodak.controller.Controller;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import org.apache.coyote.Processor;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final List<String> primitiveRequest = StringReader.readAll(bufferedReader);
            final HttpRequest httpRequest = HttpRequest.of(bufferedReader, primitiveRequest);

            final RequestMapper requestMapper = new RequestMapper();
            final Controller controller = requestMapper.mappingController(httpRequest);
            final HttpResponse httpResponse = new HttpResponse();
            if (controller == null) {
                httpResponse.notFound(httpRequest);
            } else {
                controller.service(httpRequest, httpResponse);
            }
            final String responseMessage = httpResponse.generateResponseMessage();
            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
