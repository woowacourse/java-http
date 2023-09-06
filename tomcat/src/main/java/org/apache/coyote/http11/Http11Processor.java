package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.mapper.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.apache.coyote.http11.advice.ControllerAdvice.handleException;
import static org.apache.coyote.http11.handler.adapter.HandlerAdapter.adaptController;
import static org.apache.coyote.http11.handler.mapper.HandlerMapper.getController;

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
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))
        ) {
            handleRequest(bufferedReader, bufferedWriter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(final BufferedReader bufferedReader, final BufferedWriter bufferedWriter) throws Exception {
        try {
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            Controller controller = getController(httpRequest);
            HttpResponse httpResponse = adaptController(controller, httpRequest);

            response(bufferedWriter, httpResponse);
        } catch (Exception e) {
            response(bufferedWriter, handleException(e));
        }
    }

    private void response(final BufferedWriter bufferedWriter, final HttpResponse httpResponse) throws IOException {
        bufferedWriter.write(httpResponse.toString());
        bufferedWriter.flush();
    }
}
