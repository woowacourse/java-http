package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> CONTENT_TYPE = Map.of("html", "text/html", "css", "text/css", "js", "application/javascript");
    private static final String STATIC_ROOT = "static";
    private static final String DEFAULT_REQUEST = "/";
    private static final String INDEX_REQUEST = "/index.html";
    private static final String NOT_FOUND_REQUEST = "/404.html";

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
             final InputStreamReader inputReader = new InputStreamReader(inputStream);
             final BufferedReader reader = new BufferedReader(inputReader)) {

            String requestUri = reader.readLine().split(" ")[1];

            String response = "";
            if (requestUri.equals(DEFAULT_REQUEST)) {
                requestUri = INDEX_REQUEST;
            }

            URL url = getClass().getClassLoader().getResource(STATIC_ROOT + requestUri);
            if (url == null) {
                url = getClass().getClassLoader().getResource(STATIC_ROOT + NOT_FOUND_REQUEST);
                String responseBody = new String(Files.readAllBytes(Paths.get(url.toURI())));
                String extension = getExtension(NOT_FOUND_REQUEST);

                response = "HTTP/1.1 404 Not Found \r\n" +
                        "Content-Type: " + CONTENT_TYPE.get(extension) + ";charset=utf-8 \r\n" +
                        "Content-Length: " + getContentLength(responseBody) + " \r\n\r\n" + responseBody;
            } else {
                String responseBody = new String(Files.readAllBytes(Paths.get(url.toURI())));
                String extension = getExtension(requestUri);

                response = "HTTP/1.1 200 OK \r\n" +
                        "Content-Type: " + CONTENT_TYPE.get(extension) + ";charset=utf-8 \r\n" +
                        "Content-Length: " + getContentLength(responseBody) + " \r\n\r\n" + responseBody;
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension(String requestUri) {
        int lastIndex = requestUri.lastIndexOf(".");
        return requestUri.substring(lastIndex + 1);
    }

    private int getContentLength(String responseBody) {
        return responseBody.getBytes().length;
    }
}
