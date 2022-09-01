package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.ContentType;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int REQUEST_URL_INDEX = 1;
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final String requestUrl =
                    Objects.requireNonNull(bufferedReader.readLine()).split(" ")[REQUEST_URL_INDEX];
            if (requestUrl.equals("/")) {
                outputStream.write(createHelloResponse().getBytes(StandardCharsets.UTF_8));
            }
            if (FILE_REGEX.matcher(requestUrl).matches()) {
                final String extension = requestUrl.split("\\.")[1];
                final ContentType contentType = ContentType.findContentType(extension);
                outputStream.write(createFileResponse(requestUrl, contentType).getBytes());
            }
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createFileResponse(final String requestUrl, final ContentType contentType)
            throws URISyntaxException, IOException {
        final URL resource =
                this.getClass().getClassLoader().getResource("static" + requestUrl);
        final Path path = Paths.get(resource.toURI());
        final var responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createHelloResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
    }
}
