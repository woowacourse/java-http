package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    /**
     * 단일 요청 처리 진입점
     * @param connection
     */
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = bufferedReader.readLine();

            final String[] requestParts = requestLine.split(" ");

            final String method = requestParts[0];
            final String requestUri = requestParts[1];
            final String httpVersion = requestParts[2];

            String line;
            //null: 연결이 끊겼거나 입력이 끝남
            //"": HTTP 헤더가 끝났다는 뜻
            while((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            }

            final String responseBody;

            if ("/".equals(requestUri)) {
                responseBody = "Hello world!";
            } else {
                final var resource =
                        getClass().getClassLoader().getResource("static" + requestUri);

                if (resource == null) {
                    return;
                }

                try {
                    final byte[] body =
                            Files.readAllBytes(Path.of(resource.toURI()));

                    responseBody = new String(body, StandardCharsets.UTF_8);
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }

            final String contentType;

            if (requestUri.endsWith(".css")) {
                contentType = "text/css";
            } else {
                contentType = "text/html;charset=utf-8";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " +  contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
