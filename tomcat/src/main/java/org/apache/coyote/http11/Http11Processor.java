package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LINE_SEPARATOR = System.lineSeparator();

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
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final StringBuilder requestHeader = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null || readLine.isEmpty()) {
                    break;
                }
                log.info("request line : {}", readLine);
                requestHeader.append(readLine).append(LINE_SEPARATOR);
            }

            String[] requestLines = requestHeader.toString().split(LINE_SEPARATOR);
            String[] requestLineParts = requestLines[0].split(" ");
            String requestUri = requestLineParts[1];
            log.info("request URI: {}", requestUri);

            String statusLine = "HTTP/1.1 200 OK ";
            String responseBody;

            if (requestUri.equals("/")) {
                responseBody = "Hello world!";

            } else {
                URL resource = getClass().getClassLoader().getResource("static" + requestUri);

                if (resource != null) {
                    log.info("response resource : {}", resource.toURI());
                    responseBody = new String(Files.readAllBytes(Path.of(resource.toURI())));
                } else {
                    statusLine = "HTTP/1.1 404 Not Found ";
                    responseBody = new String(Files.readAllBytes(Path.of("./static/404.html")));
                }
            }

            final var response = String.join(LINE_SEPARATOR,
                    statusLine,
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
