package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_URL = "/";
    private static final String SPACE_DELIMITER = " ";
    private static final int URL_INDEX = 1;

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
             final var outputStream = connection.getOutputStream()) {

            final var response = getOkResponse(getResponseBody(inputStream));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getOkResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + SPACE_DELIMITER,
                "",
                responseBody);
    }

    private String getResponseBody(final InputStream inputStream) throws IOException, URISyntaxException {
        final String url = parseUrl(inputStream);
        if (DEFAULT_URL.equals(url)) {
            return DEFAULT_RESPONSE_BODY;
        }
        final List<String> bodyLines = Files.readAllLines(getStaticFilePath(url));
        return String.join("\n", bodyLines) + "\n";
    }

    private String parseUrl(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String firstLine = bufferedReader.readLine();
        return firstLine.split(SPACE_DELIMITER)[URL_INDEX];
    }
    private Path getStaticFilePath(final String url) throws URISyntaxException {
        String filePathString = "static" + url;
        final URL fileUrl = getClass().getClassLoader().getResource(filePathString);
        final URI uri = Objects.requireNonNull(fileUrl).toURI();
        return Paths.get(uri);
    }
}
