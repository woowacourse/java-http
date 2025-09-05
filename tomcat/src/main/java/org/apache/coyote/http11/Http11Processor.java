package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.AbstractHandler;
import org.apache.coyote.handler.HandlerMapper;
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
             final var outputStream = connection.getOutputStream()) {

            final List<String> request = getRequest(inputStream);
            final String requestUri = getRequestUri(request);
            final String response = getResponse(requestUri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getRequest(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> request = new ArrayList<>();
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            request.add(line);
        }

        return request;
    }

    private String getRequestUri(final List<String> request) {
        final String startLine = request.getFirst();
        final String[] splitStartLine = startLine.split(" ");

        return splitStartLine[1];
    }

    public String getResponse(final String requestUri) throws IOException {
        final HandlerMapper handlerMapper = HandlerMapper.getInstance();
        final AbstractHandler handler = handlerMapper.getHandler(requestUri);
        if (handler == null) {
            return "HTTP/1.1 404 NOT FOUND ";
        }

        return handler.handle(requestUri);
    }
}
