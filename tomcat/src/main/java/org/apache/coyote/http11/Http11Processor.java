package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] str = line.split(" ", 3);
            String method = str[0];
            URI uri = URI.create(str[1]);
            String path = uri.getRawPath();

            String responseBody = "Hello world!";
            String contentType = "text/html";

            if (method.equals("GET")) {
                if (path.equals("/index.html")) {
                    responseBody = readFile("static/index.html");
                }

                if (path.equals("/css/styles.css")) {
                    responseBody = readFile("static/css/styles.css");
                    contentType = "text/css";
                }
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readFile(String fileName) throws IOException {
        try {
            var resource = getClass().getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new FileNotFoundException("리소스를 찾을 수 없습니다: " + fileName);
            }

            Path path = Path.of(resource.toURI());
            return Files.readString(path, StandardCharsets.UTF_8);

        } catch (URISyntaxException e) {
            throw new IOException("리소스 경로 변환에 실패했습니다: " + fileName, e);
        }
    }
}
