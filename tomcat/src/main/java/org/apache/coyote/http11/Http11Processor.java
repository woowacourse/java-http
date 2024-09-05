package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import com.techcourse.exception.UncheckedServletException;
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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            final var requestHeader = reader.readLine();
            final var parts = requestHeader.split(" ");
            final var method = parts[0];
            final var uri = parts[1];

            final var response = generateResponse(method, uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(String method, String uri) throws IOException {
        if (method.equals("GET") && uri.equals("/")) {
            final var responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }
        if (method.equals("GET") && ContentMimeType.isEndsWithExtension(uri)) {
            final var resource = getClass().getClassLoader().getResource("static" + uri);
            final var fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());
            final var responseBody = new String(fileContent);
            final var extension = uri.substring(uri.lastIndexOf('.') + 1);
            System.out.println("extension = " + extension);

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + ContentMimeType.getMimeByExtension(extension) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }
        throw new IllegalArgumentException("접근할 수 없습니다.");
    }
}
