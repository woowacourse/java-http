package org.apache.coyote.http11;

import com.techcourse.web.router.RequestRouter;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
             final var outputStream = connection.getOutputStream()) {

            final byte[] bytes = new byte[2048];
            inputStream.read(bytes);
            final String request = new String(bytes);
            final String response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String request) throws IOException {
        String path = getPath(request);
        String method = getMethod(request);

        return requestRouter.handleRoute(method, path);
    }

    private String getPath(final String request) {
        String requestLine = getRequestLine(request);
        String[] parts = requestLine.split(" ");
        return parts.length >= 2 ? parts[1] : "/";
    }

    private String getMethod(final String request) {
        final String requestLine = getRequestLine(request);
        final String[] parts = requestLine.split(" ");
        return parts.length >= 1 ? parts[0].toUpperCase() : "GET";
    }

    private String getRequestLine(final String request) {
        String[] lines  = request.split("\r\n");
        return lines[0].trim();
    }

}
