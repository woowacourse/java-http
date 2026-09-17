package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpRequestLine requestLine = HttpRequestLine.from(inputStream);

            HttpResponse httpResponse = handle(requestLine.uri());

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(String uri) throws IOException {
        if (uri.equals("/")) {
            return HttpResponse.of(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    "text/html;charset=utf-8",
                    "Hello world!".getBytes(StandardCharsets.UTF_8)
            );
        }

        String path = resolvePath(uri);

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return HttpResponse.of(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    contentTypeOf(path),
                    resource.readAllBytes()
            );
        }
    }

    private String resolvePath(String uri) {
        String path = "static" + uri;

        if (getClass().getClassLoader().getResource(path) == null) {
            return "static/404.html";
        }

        return path;
    }

    private String contentTypeOf(String path) {
        String contentType = URLConnection.guessContentTypeFromName(path);

        if (contentType == null) {
            return "application/octet-stream";
        }

        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }

        return contentType;
    }
}
