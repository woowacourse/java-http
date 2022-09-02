package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader headerReader = new BufferedReader(new InputStreamReader(inputStream,
                     StandardCharsets.UTF_8))) {
            final HttpRequestStartLineContents startLineContents = HttpRequestStartLineContents.from(headerReader);

            final URL resourceUrl = getResourceUrl(startLineContents.getUrl());

            final String responseBody = readContext(resourceUrl);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(startLineContents.getUrl()) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String url) {
        if (url == null) {
            return "text/html";
        }

        if (url.contains(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private URL getResourceUrl(String requestUri) {
        if (requestUri.equals("/")) {
            return null;
        }

        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }
        return getClass().getClassLoader().getResource("static" + requestUri);
    }

    private String readContext(final URL resourceUrl) throws IOException {
        if (resourceUrl == null) {
            return "Hello world!";
        }
        final File resourceFile = new File(resourceUrl.getFile());
        final Path resourcePath = resourceFile.toPath();
        final byte[] resourceContents = Files.readAllBytes(resourcePath);
        return new String(resourceContents);
    }
}
