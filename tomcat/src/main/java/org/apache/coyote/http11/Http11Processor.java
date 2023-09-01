package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_DELIMETER = "\n";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            String responseBody = "Hello world!";

            String[] headers = getHeader(inputStream).split(HEADER_DELIMETER);

            if (headers[0].contains("/index.html")) {
                URL fileUrl = this.getClass()
                        .getClassLoader()
                        .getResource("static" + "/index.html");

                Path path = new File(Objects.requireNonNull(fileUrl).getFile()).toPath();
                responseBody = new String(Files.readAllBytes(path));
            }


            String response = String.join("\r\n",
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

    private String getHeader(final InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        int c;
        while ((c = inputStream.read()) != -1) {
            stringBuilder.append((char) c);
            if (stringBuilder.toString().endsWith("\r\n\r\n")) {
                break;
            }
        }

        return stringBuilder.toString();
    }
}
