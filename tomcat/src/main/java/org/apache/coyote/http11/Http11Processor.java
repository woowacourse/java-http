package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String URL_START_REGEX = " ";
    private static final int URL_INDEX = 1;
    private static final String DEFAULT_URL = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String responseBody = getResponseBody(parseUrl(bufferedReader.readLine()));
            final String response = createResponse(responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUrl(final String request) throws IOException {
        return request.split(URL_START_REGEX)[URL_INDEX];
    }

    private String getResponseBody(final String url) throws IOException {
        if (url.equals(DEFAULT_URL)) {
            return DEFAULT_RESPONSE_BODY;
        }
        return readFile(url);
    }

    private String readFile(String url) throws IOException {
        final String filePath = "static" + url;
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        final String path = Objects.requireNonNull(resource).getPath();
        return Files.readString(Path.of(path));
    }

    private String createResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
