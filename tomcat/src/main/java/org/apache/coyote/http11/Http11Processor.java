package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC = "static/";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String WHITESPACE_REGEX = " ";

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            String[] parts = requestLine.split(WHITESPACE_REGEX);
            String part = parts[1];
            if (isRootRequest(part, outputStream)) {
                return;
            }

            String requestUri = part.substring(1);
            final String fileName = requestUri;

            URL resource = getClass().getClassLoader().getResource(STATIC + fileName);
            if (validateURLIsNull(resource, outputStream)) {
                return;
            }

            Path path = new File(resource.getPath()).toPath();

            byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.length + " ",
                    "",
                    new String(Files.readAllBytes(path)));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isRootRequest(String part, OutputStream outputStream) throws IOException {
        if (part.equals("/")) {
            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
            return true;
        }
        return false;
    }

    private static boolean validateURLIsNull(URL resource, OutputStream outputStream) throws IOException {
        if (resource == null) {
            String notFound = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Length: 0 ",
                "",
                "");

            outputStream.write(notFound.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            return true;
        }
        return false;
    }
}
