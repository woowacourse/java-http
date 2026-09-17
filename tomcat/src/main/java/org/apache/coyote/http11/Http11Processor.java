package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            String requestLine = bufferedReader.readLine();
            final String requestUri = requestLine.split(" ")[1];
            log.info("request uri: {}", requestUri);

            String responseBody;
            if (requestUri.equals("/")) {
                responseBody = "Hello world!";
            }
            else {
                URL url = getClass().getClassLoader().getResource("static/" + requestUri);
                if (url == null)
                    return;
                final Path path = Path.of(url.getPath());
                responseBody = Files.readString(path);
            }

            final String contentType = getContentType(requestUri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }

        if (requestUri.endsWith(".js")) {
            return "text/javascript; charset=utf-8";
        }

        return "text/html; charset=utf-8";
    }
}
