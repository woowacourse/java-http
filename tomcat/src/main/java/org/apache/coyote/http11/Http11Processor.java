package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

            String requestTarget = extractRequestTarget(inputStream);
            String responseBody = resolveResponseBody(requestTarget);
            String contentType = resolveContentType(requestTarget);

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

    private String extractRequestTarget(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = br.readLine();

        String[] words = line.split(" ");
        while (line != null && !line.isEmpty())  {
            line = br.readLine();
        }
        return words[1];
    }

    private String resolveResponseBody(String requestTarget) throws IOException {
        if ("/".equals(requestTarget)) {
            return "Hello world!";
        }
        String fileName = "static" + requestTarget;
        final URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("resource not found");
        }
        return readStaticResource(resource);
    }

    private String readStaticResource(URL resource) throws IOException {
        final Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }

    private String resolveContentType(String requestTarget) {
        if (requestTarget.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (requestTarget.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
