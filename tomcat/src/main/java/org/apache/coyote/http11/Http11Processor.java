package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
                    "Hello world!".getBytes()
            );
        }

        try (InputStream resource = findResource(uri)) {
            return HttpResponse.of(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    "text/html;charset=utf-8",
                    resource.readAllBytes()
            );
        }
    }

    private InputStream findResource(String uri) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resource = classLoader.getResourceAsStream("static" + uri);

        if (resource == null) {
            resource = classLoader.getResourceAsStream("static/404.html");
        }

        return resource;
    }
}
