package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String[] requestStartLine = bufferedReader.readLine().split(" ");

            final String requestTarget = requestStartLine[1];

            String responseBody = "";
            if (requestTarget.equals("/")) {
                responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.length() + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            } else if (requestTarget.equals("/index.html")) {
                final var path = Path.of("/Users/ichaeyeong/Desktop/woowacourse/level3/java-http/tomcat/src/main/resources/static", "index.html");
                final byte[] fileContent = Files.readAllBytes(path);
                responseBody = new String(fileContent, StandardCharsets.UTF_8);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + fileContent.length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            } else {
                final var path = Path.of("/Users/ichaeyeong/Desktop/woowacourse/level3/java-http/tomcat/src/main/resources/static", requestTarget);
                final byte[] fileContent = Files.readAllBytes(path);
                responseBody = new String(fileContent, StandardCharsets.UTF_8);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + fileContent.length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
