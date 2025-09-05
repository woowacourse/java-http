package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.ContentType;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

    private final Socket connection;
    private final Adapter adapter;
    private final HttpRequestParser httpRequestParser;

    public Http11Processor(final Socket connection, final Adapter adapter) {
        this.connection = connection;
        this.adapter = adapter;
        this.httpRequestParser = new HttpRequestParser();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var httpRequest = httpRequestParser.getHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }
            final var resourcePath = httpRequest.parseResourcePath();
            final var httpResponse = new Http11Response(resourcePath);
            setContentType(httpResponse.getResourcePath(), httpResponse);

            adapter.service(httpRequest, httpResponse);
            httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(httpResponse.getBody().length));
            writeResponse(httpResponse);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void setContentType(final String resourcePath, final Http11Response httpResponse) {
        final var contentType = ContentType.fromPath(resourcePath);
        httpResponse.addHeader(CONTENT_TYPE, contentType.getValue());
    }

    private void writeResponse(final Http11Response httpResponse) throws IOException, URISyntaxException {
        try (final var outputStream = connection.getOutputStream()) {
            outputStream.write(httpResponse.getResponseLine());
            outputStream.write(httpResponse.getHeader());
            outputStream.write(httpResponse.getBody());
            outputStream.flush();
        }
    }
}
