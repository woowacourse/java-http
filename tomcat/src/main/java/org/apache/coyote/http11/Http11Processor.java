package org.apache.coyote.http11;

import com.http.application.HttpRequestParser;
import com.http.application.RequestHandlerMapper;
import com.http.application.StaticResourceHandler;
import com.http.domain.HttpRequest;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final HttpRequest httpRequest = HttpRequestParser.parse(reader);
            final byte[] responseBody = StaticResourceHandler.getResponseBody(httpRequest);

            final var responseHeaders = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + parseContentType(httpRequest), "Content-Length: " + responseBody.length + " ",
                    "", "");

            RequestHandlerMapper.handle(httpRequest);

            outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseContentType(HttpRequest httpRequest) {
        final Map<String, String> headers = httpRequest.headers();
        if (!headers.containsKey("Accept")) {
            return "text/html;charset=utf-8 ";
        }

        return headers.get("Accept").split(",")[0];
    }
}
