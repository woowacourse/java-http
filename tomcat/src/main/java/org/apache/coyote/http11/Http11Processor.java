package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
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
            String responseBody = "Hello world!";
            String contentType = "";

            // 여기서 inputStream 을 body 로 만든다
            final String requestBody = parseRequestHeader(inputStream);

            // responseBody 에서 파일 이름 파싱
            final String fileName = parseFileName(requestBody);
            System.out.println(fileName);

            if (!fileName.isEmpty()) {
                responseBody = readFile(fileName);
            }

            if (fileName.endsWith(".css")) {
                contentType = "text/css;charset=utf-8 ";
            }
            if (fileName.endsWith(".html")) {
                contentType = "text/html;charset=utf-8 ";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestHeader(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder headers = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headers.append(line).append("\r\n");
        }

        return headers.toString();
    }

    private String parseFileName(String responseBody) {
        String GET = "GET /";
        String HTTP_PROTOCOL = "HTTP/1.1";

        if (!responseBody.contains(GET)) {
            return "";
        }

        return Objects.requireNonNull(extractSubstring(responseBody, GET, HTTP_PROTOCOL)).trim();
    }

    private String extractSubstring(String source, String wordA, String wordB) {
        int startIndex = source.indexOf(wordA);
        if (startIndex == -1) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }
        startIndex += wordA.length();

        int endIndex = source.indexOf(wordB, startIndex);
        if (endIndex == -1) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        return source.substring(startIndex, endIndex);
    }

    private String readFile(String fileName) throws URISyntaxException, IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("static/" + fileName);

        final File resourceFile = new File(Objects.requireNonNull(resource).toURI());
        final Path path = resourceFile.toPath();

        return new String(Files.readAllBytes(path));
    }
}
