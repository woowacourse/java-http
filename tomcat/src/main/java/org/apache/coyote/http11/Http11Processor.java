package org.apache.coyote.http11;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager = SessionManager.getInstance();
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

            performProcess(inputStream, outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void performProcess(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        try {
            HttpRequest httpRequest = new HttpRequest(inputStream);

            RequestMapping requestMapping = RequestMapping.getInstance();
            HttpResponse response = requestMapping.dispatch(httpRequest);

            outputStream.write(response.toByte());
            outputStream.flush();
        } catch (IOException | IllegalArgumentException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
