package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                return;
            }

            final String method = requestParts[0];
            final String url = requestParts[1];

            String responseBody = "Hello world!";
            String contentType = "text/html;charset=utf-8";

            // index.html 처리
            if ("GET".equals(method) && ("/".equals(url) || "/index.html".equals(url))) {
                responseBody = readIndexFile();
                contentType = ContentTypeMapper.get("index.html");
            }

            // 나머지 GET 요청 정적 리소스 처리
            if ("GET".equals(method) && !("/".equals(url) || "/index.html".equals(url))) {
                responseBody = readStaticFile(url);
                contentType = ContentTypeMapper.get(url);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readIndexFile() {
        try {
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            if (resource == null) {
                return "Hello world!";
            }
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            log.error("Failed to read index.html", e);
            return "Hello world!";
        }
    }

    private String readStaticFile(String url) {
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + url);
            if (resource == null) {
                return "File not found";
            }
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            log.error("Failed to read static file: " + url, e);
            return "File not found";
        }
    }
}
