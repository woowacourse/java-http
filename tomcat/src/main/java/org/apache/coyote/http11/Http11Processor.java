package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC_ROOT = "static";
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

            String path = extractRequestPath(inputStream);
            URL url = findStaticResource(path);
            String contentType = findContentType(path);
            final String responseBody = resolveContentOf(url);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestPath(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = br.readLine();
            Objects.requireNonNull(line);

            return line.split(" ")[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL findStaticResource(String path) {
        return getClass().getClassLoader().getResource(STATIC_ROOT + path);
    }

    private String findContentType(String path) {
        if (path.endsWith(".html") || path.endsWith("/")) {
            return "text/html";
        }

        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "text/javascript";
        }

        throw new RuntimeException("알 수 없는 Content-Type입니다.");
    }

    private String resolveContentOf(URL fileUrl) {
        if (fileUrl == null || fileUrl.getPath().endsWith("/")) {
            return "Hello world!";
        }

        try {
            Path path = new File(fileUrl.getFile()).toPath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
