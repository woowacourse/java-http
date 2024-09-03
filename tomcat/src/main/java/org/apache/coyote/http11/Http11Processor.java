package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticResourceReader staticResourceReader;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.staticResourceReader = new StaticResourceReader();
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
            List<String> requestLines = Http11InputStreamReader.read(inputStream);
            Http11ServletRequest servletRequest = Http11ServletRequest.parse(requestLines);
            String contentType = URLConnection.guessContentTypeFromName(servletRequest.path());

            final var responseBody =
                    servletRequest.path().equals("/") ?
                            "Hello world!".getBytes()
                            : staticResourceReader.read(servletRequest.path());

            final var response = responseBody == null ?
                    "HTTP/1.1 404 Not Found" :
                    String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: " + contentType + ";charset=utf-8 ",
                            "Content-Length: " + responseBody.length + " ",
                            "",
                            new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
