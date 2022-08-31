package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = createHttpRequest(inputStream);
            final String response = createResponseBy(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final InputStream inputStream) throws IOException {
        final List<String> lines = readRequestLines(inputStream);
        final String httpRequest = String.join("\r\n", lines);
        return HttpRequest.from(httpRequest);
    }

    private List<String> readRequestLines(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> lines = new ArrayList<>();

        String line = reader.readLine();
        while (!line.equals("")) {
            lines.add(line);
            line = reader.readLine();
        }
        return lines;
    }

    private String createResponseBy(HttpRequest httpRequest) throws IOException {
        final String contentType = new ContentTypeExtractor().extract(httpRequest);
        final String responseBody = getResponseBodyBy(httpRequest);

        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: " + contentType + ";charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" + responseBody;
    }

    private String getResponseBodyBy(final HttpRequest request) throws IOException {
        if (request.getUri().equals("/")) {
            return "Hello world!";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + request.getUri());
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
