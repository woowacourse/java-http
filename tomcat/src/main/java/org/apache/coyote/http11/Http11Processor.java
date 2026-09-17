package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return;
            }

            String[] requestHeader = readLine.split(" ");
            String path = requestHeader[1];
            String resourcePath = extractResourcePath(path);

            byte[] bytes = resolveResponseBody(resourcePath);
            String responseBody = new String(bytes, StandardCharsets.UTF_8);

            String contentType = resolveContentType(resourcePath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK",
                    "Content-Type: " + contentType,
                    "Content-Length: " + bytes.length,
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private byte[] resolveResponseBody(String resourcePath) throws IOException {
        if (resourcePath.equals("/")) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if (resourcePath.equals("/login")) {
            return readResource("static" + resourcePath + ".html");
        }

        return readResource("static" + resourcePath);
    }

    private String extractResourcePath(String path) {
        int index = path.indexOf("?");

        if (index == -1) {
            return path;
        }

        return path.substring(0, index);
    }

    private byte[] readResource(String resourcePath) throws IOException {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
            }

            return resourceStream.readAllBytes();
        }
    }
}
