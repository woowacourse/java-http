package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = new HttpRequestParser(inputStream).parse();
            String requestPath = request.getPath();
            String responseBody = readStaticResource(requestPath);

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType(requestPath) + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readStaticResource(String requestPath) throws IOException {
        if (!requestPath.endsWith(".html") && !requestPath.endsWith(".css")) {
            return DEFAULT_RESPONSE_BODY;
        }

        try (InputStream resource = getClass().getResourceAsStream("/static" + requestPath)) {
            if (resource == null) {
                return DEFAULT_RESPONSE_BODY;
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String contentType(String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
