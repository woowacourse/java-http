package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.FrontController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
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
            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final var httpRequest = readRequest(bufferedReader);
            final var httpResponse = getHttpResponse(HttpRequest.from(httpRequest));

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder header = new StringBuilder();

        while (bufferedReader.ready()) {
            header.append(bufferedReader.readLine())
                    .append("\r\n");
        }
        return header.toString();
    }

    private String getHttpResponse(final HttpRequest httpRequest) throws Exception {
        Controller controller = FrontController.findController(httpRequest);
        return controller.process(httpRequest).getHttpResponse();
    }
}
