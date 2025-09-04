package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final StringTokenizer requestLine = new StringTokenizer(reader.readLine());
            final String method = requestLine.nextToken();
            final URI uri = URI.create(requestLine.nextToken());
            final String protocol = requestLine.nextToken();

            String responseBody;
            String contentType = "text/html";

            if (method.equals("GET") && uri.getPath().equals("/index.html")) {
                final String filePath = getClass().getClassLoader().getResource("static/index.html").getPath();
                final File indexFile = new File(filePath);
                contentType = Files.probeContentType(Path.of(filePath));

                responseBody = new String(Files.readAllBytes(indexFile.toPath()), StandardCharsets.UTF_8);
            } else if (method.equals("GET") && uri.getPath().equals("/css/styles.css")) {
                final String filePath = getClass().getClassLoader().getResource("static/css/styles.css").getPath();
                final File cssFile = new File(filePath);
                contentType = Files.probeContentType(Path.of(filePath));

                responseBody = new String(Files.readAllBytes(cssFile.toPath()), StandardCharsets.UTF_8);
            } else if (method.equals("GET") && uri.getPath().equals("/assets/chart-area.js")) {
                final String filePath = getClass().getClassLoader().getResource("static/assets/chart-area.js").getPath();
                final File chartFile = new File(filePath);
                contentType = Files.probeContentType(Path.of(filePath));

                responseBody = new String(Files.readAllBytes(chartFile.toPath()), StandardCharsets.UTF_8);
            } else if (method.equals("GET") && uri.getPath().equals("/")) {
                responseBody = "Hello world!";
            } else {
                throw new IllegalArgumentException("지원하지 않는 요청 경로입니다: " + uri.getPath());
            }

            final var response = String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
