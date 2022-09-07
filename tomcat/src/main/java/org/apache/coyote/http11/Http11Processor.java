package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParser;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.ResponseProcessor;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
             final BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(connection.getOutputStream()))) {

            final HttpRequest httpRequest = RequestParser.createRequest(bufferedReader);
            final ResponseEntity responseEntity = Controller.processRequest(httpRequest);
            final ResponseProcessor responseProcessor = ResponseProcessor.of(httpRequest, responseEntity);
            final String response = responseProcessor.getResponse();

            bufferedWriter.write(response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
