package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.handler.HttpRequestHandlerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final HttpRequestHandlerContainer handlerContainer = new HttpRequestHandlerContainer();

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
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final String request = parseRequest(inputStream);
            final String response = processResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return bufferedReader.lines()
                .takeWhile(line -> !line.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String processResponse(String request) {
        String url = getUrl(request);
        HttpRequestHandler httpRequestHandler = handlerContainer.getHandler(url);
        if (httpRequestHandler == null) {
            throw new IllegalArgumentException("No resource found: " + url);
        }
        return httpRequestHandler.handle(request);
    }

    private String getUrl(String request) {
        return request.split(System.lineSeparator())[0].split(" ")[1].split("\\?")[0];
    }
}
