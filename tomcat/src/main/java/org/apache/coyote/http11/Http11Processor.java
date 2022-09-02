package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

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
             final BufferedReader headerReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {

            final String resourceNameLine = headerReader.readLine();

            if (resourceNameLine == null) {
                return;
            }

            final URL resourceUrl = getResourceUrl(resourceNameLine);

            final String responseBody = readContext(resourceUrl);

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getResourceUrl(final String resourceNameLine) {
        final String[] strings = resourceNameLine.split(" ");
        final String resourceUri = strings[1];

        if (resourceUri.equals("/")) {
            return null;
        }
        return getClass().getClassLoader().getResource("static" + resourceUri);
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
