package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
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

            final var responseBody = parseRequest(inputStream);

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

    private String parseRequest(InputStream inputStream) throws IOException {
        String request = new String(inputStream.readAllBytes());
        if (request.contains("GET") && !request.contains(" / ")) {
            String fileName = request.split(" ")[1];
            File file = new File(getClass().getClassLoader().getResource("static" + fileName).getFile());
            return new String(Files.readAllBytes(file.toPath()));
        }
        return "Hello world!";
    }
}
