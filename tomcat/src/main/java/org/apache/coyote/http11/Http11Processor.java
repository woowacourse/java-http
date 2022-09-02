package org.apache.coyote.http11;

import http.ContentType;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Pattern FILE_REGEX = Pattern.compile(".+\\.(html|css|js|ico)");

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            var line = bufferedReader.readLine();
            if (line == null) {
                return;
            }
            final String requestUrl = line.split(" ")[1];

            if (requestUrl.equals("/")) {
                outputStream.write(getHelloResponse().getBytes());
            }

            if (FILE_REGEX.matcher(requestUrl).matches()) {
                final String extension = requestUrl.split("\\.")[1];
                final ContentType contentType = ContentType.findContentType(extension);
                outputStream.write(createResponse(requestUrl, contentType).getBytes());
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String requestUrl, final ContentType contentType)
            throws URISyntaxException, IOException {
        final URL resource = this.getClass().getClassLoader().getResource("static" + requestUrl);
        final Path path = Paths.get(resource.toURI());
        final String responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getHelloResponse() {
        String responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
