package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            String response = handleRequest(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleRequest(HttpRequest httpRequest) throws URISyntaxException, IOException {
        if (httpRequest.isGetMethod() && httpRequest.isPath("/index.html")) {
            return createResponseBody("static/index.html", "text/html");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/login")) {
            return createResponseBody("static/login.html", "text/html");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/register")) {
            return createResponseBody("static/register.html", "text/html");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/css/styles.css")) {
            return createResponseBody("static/css/styles.css", "text/css");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/js/scripts.js")) {
            return createResponseBody("static/js/scripts.js", "text/javascript");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/assets/chart-area.js")) {
            return createResponseBody("static/assets/chart-area.js", "text/javascript");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/assets/chart-bar.js")) {
            return createResponseBody("static/assets/chart-bar.js", "text/javascript");
        }
        if (httpRequest.isGetMethod() && httpRequest.isPath("/assets/chart-pie.js")) {
            return createResponseBody("static/assets/chart-pie.js", "text/javascript");
        }
        return createResponse("Hello world!", "text/html");
    }

    private String createResponseBody(String resourcePath, String contentType) throws URISyntaxException, IOException {
        final Path path = Path.of(ClassLoader.getSystemResource(resourcePath).toURI());
        String responseBody = Files.readString(path);

        return createResponse(responseBody, contentType);
    }


    private String createResponse(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
