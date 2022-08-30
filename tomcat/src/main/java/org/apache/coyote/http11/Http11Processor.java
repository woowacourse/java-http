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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }
            final String response = getResponse(line);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(String line) throws IOException {
        final String[] split = line.split(" ");
        final String resource = split[1];

        if (resource.contains("css")) {
            return extractCssResponse(getResponseBody(resource));
        }
        if (resource.contains("js")) {
            return extractJsResponse(getResponseBody(resource));
        }

        return extractHtmlResponse(getHtmlResponseBody(resource));

    }

    private String getHtmlResponseBody(String resource) {
        final StringBuilder stringBuilder = new StringBuilder(resource);
        if (!resource.contains(".html")) {
            stringBuilder.append(".html");
        }

        return getResponseBody(stringBuilder.toString());
    }

    private String getResponseBody(String resource) {
        URL url = this.getClass().getClassLoader().getResource("static/" + resource);
        final File file = new File(url.getPath());
        final Path path = file.toPath();

        try {
            final String responseBody = Files.readString(path);
            return responseBody;
        } catch (IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    private String extractHtmlResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String extractCssResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String extractJsResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/javascript;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
