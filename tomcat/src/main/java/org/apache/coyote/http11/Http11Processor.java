package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.adapter.AdapterContext;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
            final HttpRequest request = HttpRequestParser.parse(inputStream);
            final HttpResponse response = HttpResponse.createDefault();

            log.info("요청 [HTTP 메소드: {}, 경로: {}", request.getMethod(), request.getPath());
            AdapterContext.getAdapter().service(request, response);
            log.info("응답 [HTTP 결과: {}, 헤더: {}", response.getStatusLine(), response.getHeaders());

            HttpResponseWriter.write(outputStream, response);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
