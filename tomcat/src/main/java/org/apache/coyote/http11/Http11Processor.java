package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.FrontRequestHandler;
import org.apache.coyote.http11.handler.ResponseEntity;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.support.HttpRequestParser;
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
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);

            FrontRequestHandler frontRequestHandler = new FrontRequestHandler();
            final ResponseEntity responseEntity = frontRequestHandler.handle(httpRequest);

            final HttpResponse httpResponse = HttpResponse.builder()
                    .httpStatus(responseEntity.getStatus())
                    .contentType(responseEntity.getContentType())
                    .body(responseEntity.getBody())
                    .headers(responseEntity.getHeaders())
                    .build();

            outputStream.write(httpResponse.toMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            requestUri = "/index.html";
        }
        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }
        URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        Path filePath = new File(Objects.requireNonNull(resource).getFile()).toPath();
        return new String(Files.readAllBytes(filePath));
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith("css")) {
            return "text/css";
        }
        if (requestUri.endsWith("js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
