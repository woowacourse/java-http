package org.apache.coyote.http11;

import org.apache.coyote.router.RequestRouter;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestRouter requestRouter;
    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestRouter = new RequestRouter();

    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            final String response = createResponse(requestLine);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String requestLine) throws IOException {
        final String[] parts = requestLine.split(" ");
        final String method = getMethod(parts);
        final String path = getPath(parts);

        return requestRouter.handleRoute(method, path);
    }

    private String getPath(final String[] parts) {
        return parts.length >= 2 ? parts[1] : "/";
    }

    private String getMethod(final String[] parts) {
        return parts.length >= 1 ? parts[0].toUpperCase() : "GET";
    }
}