package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
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
            // 1. HTTP 요청을 문자열 한 줄씩 읽을 수 있도록 만든다.
            final var reader = new BufferedReader(
                    new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                    )
            );

            // 2. Request Line을 읽는다.
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            // 3. "GET /index.html HTTP/1.1"에서 "/index.html"을 추출한다.
            final String[] requestLineParts = requestLine.split(" ");
            final String uri = requestLineParts[1];

            // 4. 기존 / 요청은 Hello world!를 유지한다.
            String responseBody = "Hello world!";

            // 5. /index.html 요청이면 실제 index.html을 읽는다.
            if ("/index.html".equals(uri)) {
                final URL resource = getClass()
                        .getClassLoader()
                        .getResource("static/index.html");

                final Path path = Path.of(resource.toURI());

                responseBody = Files.readString(
                        path,
                        StandardCharsets.UTF_8
                );
            }
            //System.out.println(responseBody);
            // 6. HTTP Content-Length는 body의 byte 크기다.
            final byte[] responseBodyBytes =
                    responseBody.getBytes(StandardCharsets.UTF_8);

            // 7. HTTP Response를 만든다.
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBodyBytes.length + " ",
                    "",
                    responseBody
            );

            // 8. 클라이언트에게 응답한다.
            outputStream.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );

            outputStream.flush();

        } catch (IOException
                 | URISyntaxException
                 | UncheckedServletException e) {

            log.error(e.getMessage(), e);
        }
    }
}