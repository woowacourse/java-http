package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
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

            final var httpRequestMessage = readMessage(inputStream);
            final var responseBody = createResponseBody(httpRequestMessage);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readMessage(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var stringBuilder = new StringBuilder();

        while (true) {
            final var line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

    private String createResponseBody(final String httpRequestMessage) throws IOException {
        if (httpRequestMessage.startsWith("GET /index.html HTTP/1.1")) {
            final var indexUrl = getClass().getClassLoader().getResource("static/index.html");
            return Files.readString(Path.of(indexUrl.getPath()));
        }

        return "Hello world!";
    }
}
