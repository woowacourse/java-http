package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.PathRequestHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.http11.handler.component.HttpRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final PathRequestHandler pathRequestHandler = new PathRequestHandler();
    private static final StaticResourceHandler staticResourceHandler = new StaticResourceHandler();
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
        try (
            final InputStream inputStream = connection.getInputStream();
            final InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final OutputStream outputStream = connection.getOutputStream()
        ) {
            final List<String> messageLines = List.of(bufferedReader.readLine());

            final HttpRequestMessage httpRequestMessage = HttpRequestMessage.with(messageLines);
            final String response = getResponse(httpRequestMessage.getTargetUrl());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final String targetUrl) throws IOException {
        if (pathRequestHandler.containsPath(targetUrl)) {
            return pathRequestHandler.getResponse(targetUrl).getResponse();
        }

        return staticResourceHandler.getResponse(targetUrl).getResponse();
    }
}
