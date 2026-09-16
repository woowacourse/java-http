package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String[] requestLineParts = bufferedReader.readLine().split(" ");
            final var responseBody = getResponseBody(requestLineParts[1]);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    getContentType(requestLineParts[1]),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestUri) throws IOException {
        if (!requestUri.equals("/")) {
            URL url = getClass().getClassLoader().getResource("static" + requestUri);
            File file = new File(url.getFile());
            Path path = file.toPath();
            return Files.readString(path);
        }
        return "Hello world!";
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "Content-Type: text/javascript;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }
}
