package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.headers.Extension;
import org.apache.coyote.http11.url.HandlerMapping;
import org.apache.coyote.http11.url.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static/";

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
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String uri = getUri(bufferedReader);
            String contentType = getContentType(uri);
            String responseBody = getResponseBody(uri);

            final var response = createResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String file) {
        Extension fileExtension = Extension.create(file);
        return fileExtension.getContentType();
    }

    private String getUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine()
                .split(" ")[1]
                .substring(1);
    }

    private String getResponseBody(final String uri) throws IOException, URISyntaxException {

        if (uri.isEmpty()) {
            return "Hello world!";
        }
        Url url = HandlerMapping.from(uri);
        URL resource = this.getClass()
                .getClassLoader()
                .getResource(STATIC_DIRECTORY + url.getRequest().getPath());

        validatePath(resource);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private void validatePath(URL resource) {
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("경로가 잘못 되었습니다. : null");
        }
    }

    private String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
