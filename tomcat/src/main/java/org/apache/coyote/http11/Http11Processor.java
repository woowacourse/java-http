package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_URI = "/index.html";
    private static final String STYLES_CSS = "/styles.css";
    private static final String RESOURCES_PREFIX = "static";

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            String method = line.split(" ")[0];
            String uri = line.split(" ")[1];


            final var responseBody = getResponseBody(method,uri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getExtension(uri) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String method, String uri) {
        if (uri.equals("/") && method.equals("GET")) {
            return "Hello world!";
        } else if (uri.equals(INDEX_URI) && method.equals("GET")) {
            return modelToView(INDEX_URI);
        } else if (uri.equals(STYLES_CSS) && method.equals("GET")) {
            return modelToView(STYLES_CSS);
        }
        throw new IllegalArgumentException("잘못된 주소입니다.");
    }

    private String getExtension(String uri) {
        if (uri.equals("/")) {
            return "html";
        }
        if (uri.endsWith(".html")) {
            return "html";
        }
        if (uri.endsWith(".css")) {
            return "css";
        }
        throw new IllegalArgumentException("확인이 되지 않는 확장자 입니다.");
    }

    @Nonnull
    private String modelToView(String uri) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + uri);
        try {
            return new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
