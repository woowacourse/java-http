package org.apache.coyote.http11;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            String path = extractPath(inputStream);
            String response = generateResponseWithPath(path);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractPath(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));
        String startLine = reader.readLine();
        String requestURI = startLine.split(" ")[1];

        if (requestURI.contains("?")) {
            return requestURI.substring(0, requestURI.indexOf("?"));
        }
        return requestURI;
    }

    private String generateResponseWithPath(String path) throws IOException {
        String resource = findResourceWithPath(path);
        String contentType = ContentTypeParser.parse(path);
        int contentLength = resource.getBytes().length;

        return String.join("\r\n", "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: "  + contentLength + " ",
                "",
                resource);
    }

    private String findResourceWithPath(String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        if (doesNotHaveExtension(path)) {
            path = path + ".html";
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }

    private boolean doesNotHaveExtension(String path) {
        return !path.contains(".");
    }
}
