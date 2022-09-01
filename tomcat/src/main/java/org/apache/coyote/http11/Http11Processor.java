package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequestProcessor httpRequestProcessor = new HttpRequestProcessor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> request = new ArrayList<>();
            while (bufferedReader.ready()) {
                request.add(bufferedReader.readLine());
            }
            HttpRequest httpRequest = new HttpRequest(request);
            HttpResponse httpResponse = httpRequestProcessor.process(httpRequest);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
