package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.ContentType;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String FILE_NAME_DELIMITER = ".";

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
            final List<String> request = extractRequest(inputStream);
            String responseBody = handler(extractUri(request));
            outputStream.write(writeResponseOk(ContentType.of(extractFileExtension(request)).getMediaType(), responseBody));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> extractRequest(InputStream inputStream) throws IOException {
        List<String> request = new ArrayList<>();
        final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            request.add(line);
        }
        return request;
    }

    private String extractUri(List<String> request) {
        return request.get(0).split(" ")[1];
    }

    private String extractFileExtension(List<String> request) {
        final int NO_EXISTING = -1;
        String uri = extractUri(request);
        int fileNameDelimiterIndex = uri.lastIndexOf(FILE_NAME_DELIMITER);
        if (fileNameDelimiterIndex == NO_EXISTING) {
            return ContentType.TEXT_HTML.getFileExtension();
        }
        return uri.substring(fileNameDelimiterIndex + 1);
    }

    private String handler(String uri) throws IOException {
        if ("/".equals(uri)) {
            return "Hello world!";
        }
        return readFile(getResource(uri));
    }

    private String readFile(URL resource) throws IOException {
        return new String(Files.readAllBytes(new File(resource.getFile())
            .toPath()));
    }

    private URL getResource(String uri) {
        return getClass().getClassLoader()
            .getResource("static" + uri);
    }

    private byte[] writeResponseOk(String contentType, String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody).getBytes();
    }
}
