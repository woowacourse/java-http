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

            final var reader = new BufferedReader(
                    new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                    )
            );

            // 1. Request Line 읽기
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            // GET /index.html HTTP/1.1
            final String[] requestLineParts = requestLine.split(" ");
            final String uri = requestLineParts[1];

            String responseBody;
            String contentType;

            // 2. 기존 기본 요청
            if ("/".equals(uri)) {
                responseBody = "Hello world!";
                contentType = "text/html;charset=utf-8";
            } else {

                // 3. URI를 classpath resource 경로로 변환
                final String resourcePath = "static" + uri;

                final URL resource = getClass()
                        .getClassLoader()
                        .getResource(resourcePath);

                final Path path = Path.of(resource.toURI());

                // 4. 파일 읽기
                responseBody = Files.readString(
                        path,
                        StandardCharsets.UTF_8
                );

                // 5. Content-Type 결정
                if (uri.endsWith(".css")) {
                    contentType = "text/css";
                } else {
                    contentType = "text/html;charset=utf-8";
                }
            }

            final byte[] responseBodyBytes =
                    responseBody.getBytes(StandardCharsets.UTF_8);

            // 6. HTTP Response 생성
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBodyBytes.length + " ",
                    "",
                    responseBody
            );

            // 7. 응답 전송
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