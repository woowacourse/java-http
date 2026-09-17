package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8));

            //첫줄 헤더 읽기
            final String requestLine = reader.readLine();
            if (requestLine == null) return;

            //uri 분리
            final String uri = requestLine.split(" ")[1];

            //나머지 헤더 읽기 - 레벨1은 안씀
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {

            }

            final byte[] responseBody = createResponseBody(uri);
            if (responseBody == null) {
                return;
            }

            final var responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + resolveContentType(uri) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");


            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveContentType(final String uri) {
        if (uri.endsWith(".css")) {
            return "text/css";
        }

        return "text/html";
    }

    private byte[] createResponseBody(final String uri) throws IOException {
        if ("/".equals(uri)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        return readResource(uri);
    }

    private byte[] readResource(final String uri) throws IOException {
        try (InputStream resource = getClass().getClassLoader()
                .getResourceAsStream("static" + uri)) {
            if (resource == null) {
                return null;
            }

            return resource.readAllBytes();
        }
    }
}
