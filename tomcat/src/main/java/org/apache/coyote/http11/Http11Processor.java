package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
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
             final var outputStream = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String requestStartLine = br.readLine();
            String url = requestStartLine.split(" ")[1];

            String response;
            if ("/".equals(url)) {
                response = createHtmlResponse("Hello world!");
            } else {
                Path staticResource = getStaticResource(url);
                response = createHtmlResponse(Files.readString(staticResource));
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Path getStaticResource(String url) {
        URL resourceURL = getClass().getClassLoader().getResource("static" + url);
        if (resourceURL == null) {
            throw new RuntimeException("리소스가 존재하지 않습니다. 경로:" + url);
        }
        return Path.of(resourceURL.getFile());
    }

    private String createHtmlResponse(String body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
