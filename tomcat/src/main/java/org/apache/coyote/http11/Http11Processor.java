package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.*;

public class Http11Processor implements Runnable, Processor {

    private static final int LOCATION_INDEX = 1;
    private static final String HTTP_HEADER_DELIMITER = " ";
    private static final String BASE_PATH = "static";
    private static final String ROOT_RESPONSE = "Hello world!";
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

            final String body = makeResponseBody(inputStream);
            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponseBody(final InputStream inputStream) throws IOException {
        final Path path = getPath(inputStream);

        if (Files.isDirectory(path)) {
            return ROOT_RESPONSE;
        }
        return String.join("\n", Files.readAllLines(path))+"\n";
    }

    private Path getPath(final InputStream inputStream) throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(BASE_PATH + getLocation(inputStream));

        if (isNull(resource)) {
            throw new IllegalArgumentException("존재하지 않는 자원입니다.");
        }
        return Paths.get(resource.getPath());
    }

    private String getLocation(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final String header = br.readLine();

        return header.split(HTTP_HEADER_DELIMITER)[LOCATION_INDEX];
    }
}
