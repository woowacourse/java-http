package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            HttpRequest request = HttpRequest.from(inputStream);

            StaticFileHandler fileHandler = new StaticFileHandler();
            HttpResponse response;
            
            if (fileHandler.exists(request.getPath())) {
                String fileContent = fileHandler.readFile(request.getPath());
                response = HttpResponse.ok(fileContent);
            } else {
                response = HttpResponse.notFound();
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
            
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
