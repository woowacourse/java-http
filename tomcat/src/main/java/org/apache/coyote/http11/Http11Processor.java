package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
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

            String rawRequest = readHttpRequest(new BufferedReader(new InputStreamReader(inputStream)));
            MyHttpRequest httpRequest = MyHttpRequest.of(rawRequest);
            log.info("start: {}", httpRequest);

            URL fileUrl = this.getClass().getClassLoader().getResource(httpRequest.resourcePath);
            File file = new File(Objects.requireNonNull(fileUrl).toURI());
            String responseBody = "Hello world!";
            if (file.isFile()) {
                responseBody = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            }

            // 공통 응답
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + httpRequest.contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
            log.info("end: {}", httpRequest);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String readHttpRequest(BufferedReader br) throws IOException {
        final StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            sb.append(line).append("\r\n");
        }
        return sb.toString();
    }

    record MyHttpRequest(String method, String resourcePath, String contentType, String version) {

        private static final String RESOURCE_PATH_PREFIX = "static";

        static MyHttpRequest of(String rawRequest) {
            String requestLine = rawRequest.lines()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("http 요청을 읽을 수 없습니다."));
            String[] split = requestLine.split(" ");
            return new MyHttpRequest(
                    split[0],
                    RESOURCE_PATH_PREFIX + split[1],
                    contentTypeOf(split[1]),
                    split[2]
            );
        }

        private static String contentTypeOf(String url) {
            int lastDotIndex = url.lastIndexOf(".");
            String fileNameExtension = url.substring(lastDotIndex + 1);
            return switch (fileNameExtension) {
                case "/", "html" -> "text/html";
                case "css" -> "text/css";
                case "js" -> "text/javascript";
                case "ico" -> "image/x-icon";
                default -> "text/plain";
            };
        }
    }
}
