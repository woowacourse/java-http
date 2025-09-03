package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";

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

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String firstLineOfRequestHeader = br.readLine();
            String requestUri = firstLineOfRequestHeader.split(" ")[1];
            // 헤더
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    break;
                }
            }

            // 바디
            var responseBody = getResponseBody(requestUri);
            var contentType = getContentType(requestUri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: %s;charset=utf-8 ".formatted(contentType),
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
        if (requestUri.equals("/")) {
            return "Hello world!";
        }
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + requestUri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".html")) {
            return "text/html";
        }
        return "text/plain";
    }
}
