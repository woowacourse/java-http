package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final int REQUEST_TARGET_INDEX = 1;
    public static final String STATIC = "static";
    private static final String CSS_EXTENSION = ".css";
    public static final String HTTP_1_1_200_OK = "HTTP/1.1 200 OK ";
    public static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html;charset=utf-8 ";
    public static final String CONTENT_TYPE_CSS = "Content-Type: text/css;charset=utf-8 ";

    public static final String CONTENT_LENGTH = "Content-Length: ";
    public static final String HELLO_WORLD = "Hello world!";
    public static final String HOME_PATH = "/";
    public static final String CRLF = "\r\n";

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder();

            String line = bufferedReader.readLine();
            while (!"".equals(line)) {
                stringBuilder.append(line).append(CRLF);
                if (line == null) {
                    return;
                }
                line = bufferedReader.readLine();
            }

            String requestTarget = stringBuilder.toString().split(" ")[REQUEST_TARGET_INDEX];
            final String responseBody = getResponseBody(requestTarget);

            String contentType = getContentType(requestTarget);

            final var response = String.join(CRLF,
                    HTTP_1_1_200_OK,
                    contentType,
                    CONTENT_LENGTH + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String requestTarget) {
        if (requestTarget.endsWith(CSS_EXTENSION)) {
            return CONTENT_TYPE_CSS;
        }
        return CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8;
    }

    private String getResponseBody(String requestTarget) throws URISyntaxException, IOException {
        if (Objects.equals(requestTarget, HOME_PATH)) {
            return HELLO_WORLD;
        }

        final URL resource = getClass().getClassLoader().getResource(STATIC + requestTarget);
        final Path path = Paths.get(Objects.requireNonNull(resource).toURI());

        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}
