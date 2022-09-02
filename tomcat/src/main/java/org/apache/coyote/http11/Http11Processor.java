package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.NotFoundException;
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
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            OutputStream outputStream = connection.getOutputStream();

            // receiving http request
            String requestMessage = parseRequest(bufferedReader);
            HttpRequest httpRequest = HttpRequest.from(requestMessage);

            // handling request
            String uri = httpRequest.getRequestTarget().getUri();
            Handler handler = HandlerMapper.lookUp(uri);
            String responseMessage = handler.handle(httpRequest);

            // sending http response
            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | NotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine())
                    .append("\r\n");
        }
        return stringBuilder.toString();
    }
}
