package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String defaultRequest = "/";
    private static final String indexRequest = "/index.html";

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
            if (requestUri.equals(defaultRequest)) {
                final var responseBody = "Hello world!";

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                
            } else if (requestUri.equals(indexRequest)) {
                URL url = getClass().getClassLoader().getResource("static/index.html");

                response = "HTTP/1.1 200 OK \r\n" +
                        "Content-Type: text/html;charset=utf-8 \r\n" +
                        "Content-Length: 5564 \r\n" +
                        "\r\n"+
                        new String(Files.readAllBytes(Paths.get(url.toURI())));
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
