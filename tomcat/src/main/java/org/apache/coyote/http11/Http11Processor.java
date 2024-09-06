package org.apache.coyote.http11;

import static org.apache.coyote.http11.http.MediaType.TEXT_CSS;
import static org.apache.coyote.http11.http.MediaType.TEXT_HTML;

import java.io.IOException;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.Headers;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatusCode;
import org.apache.coyote.http11.resource.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var request = new HttpRequest(inputStream);
            log.info("request: \n{}", request);

            final var body = ResourceReader.readString(request.getPath());

            final var headers = new Headers();
            final var extension = extension(request.getPath());
            final var mediaType = mediaType(extension);
            headers.put("Content-Type", mediaType);
            headers.put("Content-Type", "charset=utf-8");
            headers.put("Content-Length", String.valueOf(body.getBytes().length));

            final var httpResponse = new HttpResponse(
                    request.getHttpVersion(),
                    HttpStatusCode.OK,
                    headers,
                    body
            );

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extension(final String path) {
        final var index = path.lastIndexOf(".");
        return path.substring(index + 1);
    }

    private String mediaType(final String extension) {
        if (extension.equals("css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
