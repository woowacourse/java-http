package org.apache.coyote.http11;

import http.BasicHttpRequest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.ui.MainController;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final MainController mainController;

    public Http11Processor(final Socket connection, final MainController mainController) {
        this.connection = connection;
        this.mainController = mainController;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        ) {
            final var httpMessageRequest = parseRequest(bufferedReader);
            final var httpRequest = BasicHttpRequest.from(httpMessageRequest);
            final var httpResponse = mainController.doService(httpRequest);
            final var httpMessageResponse = httpResponse.getResponseHttpMessage();

            bufferedWriter.write(httpMessageResponse);
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequest(final BufferedReader bufferedReader) throws IOException {
        final var request = new StringBuilder();

        while (bufferedReader.ready()) {
            request.append(String.format("%s%s", bufferedReader.readLine(), System.lineSeparator()));
        }

        return request.toString();
    }
}
