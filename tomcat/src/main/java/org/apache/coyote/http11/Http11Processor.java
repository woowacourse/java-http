package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpParser;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpParser.getRequest(inputStream);
            log.info("request: {}", httpRequest);

            dispatch(httpRequest, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dispatch(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        String path = httpRequest.getPathWithoutExtension();
        Controller controller = RequestMapping.getController(path);
        log.info("path: {}, controller: {}", path, controller);
        try {
            String response = controller.service(httpRequest);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
