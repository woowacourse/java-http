package org.apache.coyote.http11;

import org.apache.catalina.RequestHandler;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestHandler requestHandler = new RequestHandler();

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
            HttpResponse response;
            HttpRequest request;
            try {
                request = new HttpRequest(inputStream);
                response = requestHandler.handle(request);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                byte[] body = Files.readAllBytes(get400ErrorPage());
                response = HttpResponse.badRequest()
                        .contentType(ContentType.TEXT_HTML)
                        .contentLength(body.length)
                        .body(body)
                        .build();
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Path get400ErrorPage() {
        URL resourceURL = getClass().getClassLoader().getResource("static/400.html");
        return Path.of(resourceURL.getFile());
    }
}
