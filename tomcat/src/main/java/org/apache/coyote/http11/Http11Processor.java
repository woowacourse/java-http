package org.apache.coyote.http11;

import com.techcourse.PathMatcher;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final PathMatcher pathMatcher;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.pathMatcher = new PathMatcher(new ResourcesReader());
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

            final HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

            final var resource = pathMatcher.match(httpRequest);

            final HttpResponse response = ok(resource);
            HttpResponseWriter.write(outputStream, response);
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse ok(final Resource resource){
        final Headers headers = new Headers();
        headers.put("Content-Type: " + getContentType(resource.getExtension()));
        headers.put("Content-Length: " + resource.length() + " ");
        return new HttpResponse(HttpStatusCode.OK, headers, "HTTP/1.1", resource.getBytes());
    }

    private String getContentType(final FileExtension extension) {
        if (extension.equals(FileExtension.HTML)) {
            return "text/html;charset=utf-8 ";
        } else if (extension.equals(FileExtension.CSS)) {
            return "text/css; ";
        }
        return "text/plain;charset=utf-8 ";
    }
}
