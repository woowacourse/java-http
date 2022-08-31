package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final int URL_INDEX = 1;
    public static final String LANDING_PAGE_URL = "/";
    public static final String STATIC_PATH = "static";

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
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String uri = parseUri(bufferedReader);
            String responseBody = accessUri(uri);

            String response = toResponse(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().split(" ", -1)[URL_INDEX];
    }

    private String accessUri(final String uri) throws IOException {
        if (uri.equals(LANDING_PAGE_URL)) {
            return "Hello world!";
        }
        return readResource(STATIC_PATH + uri);
    }

    private String readResource(final String resourcePath) throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        assert resource != null;
        Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }

    private String toResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
