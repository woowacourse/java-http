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

            StartLine startLine = extractStartLine(inputStream);
            AbsolutePath absolutePath = startLine.absolutePath();

            if (startLine.hasQueryParameters()) {
                String[] queryParameters = startLine.queryParameters();
                log.info(queryParameters[0]);
                log.info(queryParameters[1]);
            }
            String response = generateResponseWithPath(absolutePath);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private StartLine extractStartLine(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));
        return StartLine.from(reader.readLine());
    }

    private String generateResponseWithPath(AbsolutePath absolutePath) throws IOException {
        String resource = findResourceWithPath(absolutePath);
        String contentType = ContentTypeParser.parse(absolutePath.value());
        int contentLength = resource.getBytes().length;

        return String.join("\r\n", "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: "  + contentLength + " ",
                "",
                resource);
    }

    private String findResourceWithPath(AbsolutePath absolutePath) throws IOException {
        if (absolutePath.isRootDirectory()) {
            return "Hello world!";
        }
        URL resourceUrl = getClass().getClassLoader().getResource("static" + absolutePath.value());
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }
}
